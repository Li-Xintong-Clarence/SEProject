import bcrypt

# init.sql 里的预置哈希
admin_hash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"
testuser_hash = "$2a$10$YQH4x5k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"

# 生成新的正确哈希
new_admin_hash = bcrypt.hashpw(b'admin123', bcrypt.gensalt()).decode()
new_user_hash = bcrypt.hashpw(b'user123', bcrypt.gensalt()).decode()

print("=== 验证预置哈希是否匹配 ===")
print(f"admin123 vs init.sql admin hash:  {bcrypt.checkpw(b'admin123', admin_hash.encode())}")
print(f"user123 vs init.sql testuser hash: {bcrypt.checkpw(b'user123', testuser_hash.encode())}")

print("\n=== 正确的哈希值（用于替换 init.sql） ===")
print(f"admin123  ->  {new_admin_hash}")
print(f"user123   ->  {new_user_hash}")
