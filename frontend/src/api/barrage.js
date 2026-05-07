import request from './request'

// 获取图书弹幕（公开）
export function getBookBarrages(bookId, limit) {
  return request.get(`/barrages/public/book/${bookId}`, { params: { limit } })
}

// 获取最新弹幕（公开）
export function getLatestBarrages(limit) {
  return request.get('/barrages/public/latest', { params: { limit } })
}

// 分页查询弹幕（管理员）
export function getBarrages(params) {
  return request.get('/barrages', { params })
}

// 发送弹幕
export function sendBarrage(data) {
  return request.post('/barrages', data)
}

// 切换弹幕状态
export function toggleBarrageStatus(id) {
  return request.put(`/barrages/${id}/toggle`)
}

// 删除弹幕
export function deleteBarrage(id) {
  return request.delete(`/barrages/${id}`)
}
