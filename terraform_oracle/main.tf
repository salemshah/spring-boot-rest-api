########################################
# main.tf (fixed)
########################################
# Get ADs (zones) in the region
data "oci_identity_availability_domains" "ads" {
  compartment_id = var.tenancy_ocid
}

# Reusable locals
locals {
  use_a1   = lower(var.shape) == lower("VM.Standard.A1.Flex")
  ssh_cidr = coalesce(var.my_ip, "0.0.0.0/0")
}

# VCN + Subnet (public) + IGW + Route + Security Lists
resource "oci_core_vcn" "vcn" {
  count          = var.create_vcn ? 1 : 0
  cidr_block     = var.vcn_cidr
  compartment_id = var.compartment_ocid
  display_name   = "free-tier-vcn"
  dns_label      = "freevcn"   # ✅ required if subnet uses dns_label
}

resource "oci_core_internet_gateway" "igw" {
  count          = var.create_vcn ? 1 : 0
  compartment_id = var.compartment_ocid
  vcn_id         = oci_core_vcn.vcn[0].id
  enabled        = true
  display_name   = "igw"
}

resource "oci_core_route_table" "rt" {
  count          = var.create_vcn ? 1 : 0
  compartment_id = var.compartment_ocid
  vcn_id         = oci_core_vcn.vcn[0].id
  display_name   = "rt-public"

  route_rules {
    destination       = "0.0.0.0/0"
    destination_type  = "CIDR_BLOCK"
    network_entity_id = oci_core_internet_gateway.igw[0].id
  }
}

resource "oci_core_security_list" "seclist" {
  count          = var.create_vcn ? 1 : 0
  compartment_id = var.compartment_ocid
  vcn_id         = oci_core_vcn.vcn[0].id
  display_name   = "seclist-public"

  # Allow all egress
  egress_security_rules {
    protocol         = "all"
    destination      = "0.0.0.0/0"
    destination_type = "CIDR_BLOCK"
    stateless        = false
  }

  # SSH ingress
  ingress_security_rules {
    protocol    = "6" # TCP
    source      = local.ssh_cidr
    source_type = "CIDR_BLOCK"
    tcp_options {
      min = 22
      max = 22
    }
    stateless = false
  }

  # Optional HTTP/HTTPS ingress
  dynamic "ingress_security_rules" {
    for_each = var.allow_http_https ? [80, 443] : []
    content {
      protocol    = "6"
      source      = "0.0.0.0/0"
      source_type = "CIDR_BLOCK"
      tcp_options {
        min = ingress_security_rules.value
        max = ingress_security_rules.value
      }
      stateless = false
    }
  }
}

resource "oci_core_subnet" "subnet" {
  count                      = var.create_vcn ? 1 : 0
  compartment_id             = var.compartment_ocid
  vcn_id                     = oci_core_vcn.vcn[0].id
  cidr_block                 = var.subnet_cidr
  display_name               = "public-subnet"
  route_table_id             = oci_core_route_table.rt[0].id
  security_list_ids          = [oci_core_security_list.seclist[0].id]
  prohibit_public_ip_on_vnic = false
  dns_label                  = "pub"
}

# Find a recent image compatible with the shape
data "oci_core_images" "images" {
  compartment_id           = var.compartment_ocid
  operating_system         = var.os_name
  operating_system_version = var.os_version_prefix == "" ? null : var.os_version_prefix
  shape                    = var.shape
  sort_by                  = "TIMECREATED"
  sort_order               = "DESC"
  state                    = "AVAILABLE"
}

# Launch instances
resource "oci_core_instance" "vm" {
  count               = var.instance_count
  availability_domain = data.oci_identity_availability_domains.ads.availability_domains[(count.index) % length(data.oci_identity_availability_domains.ads.availability_domains)].name
  compartment_id      = var.compartment_ocid
  display_name        = "${var.hostname_prefix}-${count.index + 1}"
  shape               = var.shape

  dynamic "shape_config" {
    for_each = local.use_a1 ? [1] : []
    content {
      ocpus         = var.ocpus
      memory_in_gbs = var.memory_in_gbs
    }
  }

  source_details {
    source_type             = "image"
    source_id               = data.oci_core_images.images.images[0].id
    boot_volume_size_in_gbs = var.boot_volume_size_gbs
  }

  create_vnic_details {
    assign_public_ip = true
    subnet_id        = var.create_vcn ? oci_core_subnet.subnet[0].id : null
  }

  metadata = {
    ssh_authorized_keys = var.ssh_public_key
    user_data = base64encode(<<-CLOUDINIT
      #cloud-config
      package_update: true
      package_upgrade: false
      runcmd:
        - echo "Hello from Terraform & OCI Free Tier" > /etc/motd
CLOUDINIT
    )
  }
}
