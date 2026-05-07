import request from './request'

// 获取所有分类
export function getCategories(keyword) {
  return request.get('/categories', { params: { keyword } })
}

// 获取分类详情
export function getCategoryById(id) {
  return request.get(`/categories/${id}`)
}

// 创建分类
export function createCategory(data) {
  return request.post('/categories', data)
}

// 更新分类
export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

// 删除分类
export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}
