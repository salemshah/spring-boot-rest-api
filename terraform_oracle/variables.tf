########################################
# variables.tf (fixed)
########################################

variable "tenancy_ocid" {}
variable "compartment_ocid" {}
variable "user_ocid" {}
variable "fingerprint" {}
variable "private_key_path" {}

variable "region" {
  default = "eu-frankfurt-1"
}

variable "ssh_public_key" {
  description = "Your SSH public key content (e.g., contents of ~/.ssh/id_rsa.pub)"
  type        = string
}

variable "create_vcn" {
  type    = bool
  default = true
}

variable "vcn_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "subnet_cidr" {
  type    = string
  default = "10.0.1.0/24"
}

variable "my_ip" {
  description = "Your public IP in CIDR (e.g., 203.0.113.7/32). If empty, SSH will be open to the world."
  type        = string
  default     = null
}

variable "allow_http_https" {
  type    = bool
  default = false
}

# Instance settings (Always Free shapes)
variable "instance_count" {
  type    = number
  default = 1
}

variable "shape" {
  description = "Use VM.Standard.A1.Flex (ARM) or VM.Standard.E2.1.Micro (x86)"
  type        = string
  default     = "VM.Standard.A1.Flex"
}

variable "ocpus" {
  description = "Only used when shape is *A1.Flex*. Stay within Free Tier total (<=4 OCPU per tenancy)."
  type        = number
  default     = 1
}

variable "memory_in_gbs" {
  description = "Only used when shape is *A1.Flex*. Stay within Free Tier total (<=24 GB per tenancy)."
  type        = number
  default     = 6
}

variable "boot_volume_size_gbs" {
  type    = number
  default = 50 # keep within 200 GB free quota total
}

variable "os_name" {
  description = "Operating system family for the image search"
  type        = string
  default     = "Canonical Ubuntu"
}

variable "os_version_prefix" {
  description = "Filter OS version that starts with this (e.g., 22.04). Leave empty to take latest."
  type        = string
  default     = "22.04"
}

variable "hostname_prefix" {
  type    = string
  default = "freevm"
}
