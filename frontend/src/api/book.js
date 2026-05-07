import request from './request'

// 分页查询图书
export function getBooks(params) {
  return request.get('/books', { params })
}

// 获取所有图书
export function getAllBooks() {
  return request.get('/books/all')
}

// 获取图书详情
export function getBookById(id) {
  return request.get(`/books/${id}`)
}

// 创建图书
export function createBook(data) {
  return request.post('/books', data)
}

// 更新图书
export function updateBook(id, data) {
  return request.put(`/books/${id}`, data)
}

// 删除图书
export function deleteBook(id) {
  return request.delete(`/books/${id}`)
}
