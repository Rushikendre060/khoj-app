module "vpc" {
  source = "./modules/vpc"
}

module "eks" {
  source       = "./modules/eks"
  cluster_name = var.cluster_name
  subnet_ids   = module.vpc.private_subnets   # ✅ IMPORTANT
}
