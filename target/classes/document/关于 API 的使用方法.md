## 关于 API 的使用方法

#### #### 如何发送一个基本的 API 请求？

**答：**
 你可以使用工具如 `curl`、Postman 或在代码中使用 HTTP 客户端（如 Java 的 `HttpClient`、Python 的 `requests`）来发送请求。确保提供正确的 URL、HTTP 方法（如 GET/POST）和请求头（如 `Content-Type: application/json`）。

------

#### #### API 的请求方式有哪些？我该怎么选？

**答：**
 常见的请求方式包括：

- `GET`：获取数据（无副作用）
- `POST`：提交数据（如注册、登录）
- `PUT`：更新数据（全量更新）
- `PATCH`：更新数据（部分更新）
- `DELETE`：删除数据

选择依据是操作的语义，比如查询用 GET，新增用 POST。

------

#### #### API 路径中的参数该如何填写？

**答：**
 路径参数通常位于 URL 中，如 `/api/user/{id}`。你需要将 `{id}` 替换为实际的值，例如 `/api/user/123`。查询参数则是 `?key=value` 形式。

------

#### #### 使用 API 时需要身份验证吗？

**答：**
 取决于系统是否开启了认证机制。常见做法是使用 API Key、Token（如 JWT），需要将 Token 放入请求头中，例如：

```
makefile


复制编辑
Authorization: Bearer <your-token>
```

------

#### #### 如何判断 API 是否调用成功？

**答：**
 主要看返回的 HTTP 状态码：

- 2xx 表示成功（如 200 OK）
- 4xx 表示客户端错误（如 400 Bad Request）
- 5xx 表示服务器错误（如 500 Internal Server Error）

另外，也应关注返回体中的 `code` 字段和 `message` 提示。