import bcrypt

# 生成正确的哈希
new_admin_hash = bcrypt.hashpw(b'admin123', bcrypt.gensalt()).decode()
new_user_hash = bcrypt.hashpw(b'user123', bcrypt.gensalt()).decode()

print("正确的哈希值:")
print(f"admin: {new_admin_hash}")
print(f"testuser: {new_user_hash}")
