import request from '@/utils/request'

export function getDepots() {
  return request({
    url: '/api/depots',
    method: 'get'
  })
}

export function getActiveDepots() {
  return request({
    url: '/api/depots/active',
    method: 'get'
  })
}

export function getAvailableDepots() {
  return request({
    url: '/api/depots/available',
    method: 'get'
  })
}

export function getDepotById(id) {
  return request({
    url: `/api/depots/${id}`,
    method: 'get'
  })
}

export function createDepot(data) {
  return request({
    url: '/api/depots',
    method: 'post',
    data
  })
}

export function updateDepot(id, data) {
  return request({
    url: `/api/depots/${id}`,
    method: 'put',
    data
  })
}

export function deleteDepot(id) {
  return request({
    url: `/api/depots/${id}`,
    method: 'delete'
  })
}
