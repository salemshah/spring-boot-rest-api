########################################
# README.md (quick start)
########################################
# 1) نصب Terraform و تنظیم OCI CLI/Keys
#    - کلیدهای API کاربر را بسازید و مقادیر tenancy/user/fingerprint/private_key_path را بردارید.
# 2) فایل terraform.tfvars را از نمونه پر کنید.
# 3) اجرای پروژه:
#    terraform init
#    terraform plan
#    terraform apply
# 4) بعد از ساخت، خروجی‌ها (IP و دستور SSH) را می‌بینی.
# 5) رعایت سقف‌های Free Tier:
#    - اگر A1 هستی، مجموع OCPU ≤ 4 و RAM ≤ 24GB بین همه VMها.
#    - برای E2.1.Micro حداکثر 2 عدد.
#    - مجموع حجم Block Volume (Boot + Data) ≤ 200GB.
# 6) پاک‌سازی منابع در صورت عدم نیاز:
#    terraform destroy
