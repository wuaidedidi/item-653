import request from './request'

// 登录
export function login(data) {
  return request.post('/auth/login', data)
}

// 注册
export function register(data) {
  return request.post('/auth/register', data)
}

// 登出
export function logout() {
  return request.post('/auth/logout')
}

// 获取当前用户信息
export function getCurrentUser() {
  return request.get('/users/me')
}

// 更新当前用户信息
export function updateCurrentUser(data) {
  return request.put('/users/me', data)
}

// 修改密码
export function changePassword(data) {
  return request.put('/users/me/password', data)
}
