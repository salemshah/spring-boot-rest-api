########################################
# outputs.tf
########################################
output "instance_public_ips" {
  value = [for i in oci_core_instance.vm : i.public_ip]
}

output "ssh_commands" {
  description = "Copy/paste SSH commands (Ubuntu user shown; use 'opc' for Oracle Linux)."
  value       = [for i in oci_core_instance.vm : format("ssh ubuntu@%s", i.public_ip)]
}
