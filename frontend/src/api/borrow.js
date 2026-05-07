import request from './request'

// 分页查询借阅记录
export function getBorrowRecords(params) {
  return request.get('/borrows', { params })
}

// 获取我的借阅记录
export function getMyBorrowRecords() {
  return request.get('/borrows/my')
}

// 借书
export function borrowBook(bookId, days) {
  return request.post('/borrows/borrow', null, { params: { bookId, days } })
}

// 还书
export function returnBook(id) {
  return request.post(`/borrows/return/${id}`)
}

// 续借
export function renewBook(id, days) {
  return request.post(`/borrows/renew/${id}`, null, { params: { days } })
}
