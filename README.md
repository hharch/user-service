# User Service

Spring Boot 用户服务示例，提供用户注册、登录、JWT 鉴权、角色和权限配置能力。

## 功能

- 用户注册：`POST /api/auth/register`
- 用户登录：`POST /api/auth/login`，返回 Bearer JWT
- 查询当前用户：`GET /api/users/me`
- 管理员角色列表：`GET /api/admin/roles`
- 管理员创建/更新角色权限：`POST /api/admin/roles`
- 管理员给用户分配角色：`PUT /api/admin/users/{userId}/roles`

启动时会自动创建：

- 默认普通角色：`USER`，权限 `PROFILE_READ`
- 默认管理员角色：`ADMIN`，权限 `ROLE_READ`、`ROLE_WRITE`、`USER_ROLE_WRITE`
- 默认管理员账号：`admin / admin123`

> 生产环境请通过环境变量 `ADMIN_PASSWORD` 和 `JWT_SECRET` 覆盖默认配置。

## 运行

```bash
mvn spring-boot:run
```

## 示例请求

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123"}'

TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"secret123"}' | jq -r '.accessToken')

curl http://localhost:8080/api/users/me -H "Authorization: Bearer $TOKEN"
```

## 测试

```bash
mvn test
```
