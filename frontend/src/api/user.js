import request from './request'

// 分页查询用户
export function getUsers(params) {
  return request.get('/users', { params })
}

// 获取用户详情
export function getUserById(id) {
  return request.get(`/users/${id}`)
}

// 更新用户
export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}
