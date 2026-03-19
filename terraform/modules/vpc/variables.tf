variable "cidr_block" {
  description = "VPC CIDR Block"
  default = "10.0.0.0/16"
}

variable "az_count" {
  default = 2
}