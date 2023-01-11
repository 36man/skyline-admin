import request from '@/utils/request'

export function getConfig(params) {
  return request({
    url: '/vue-admin-template/demo/vueFile',
    method: 'get',
    params
  })
}
