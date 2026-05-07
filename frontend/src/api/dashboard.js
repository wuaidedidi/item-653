import request from './request'

// 获取仪表盘统计数据
export function getStatistics() {
  return request.get('/dashboard/statistics')
}
