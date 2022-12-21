import request from '@/utils/request'

export function getConfig(params) {
  return request({
    url: '/vue-admin-template/demo/demo',
    method: 'get',
    params
  })
}
