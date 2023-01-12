import request from '@/utils/request'

export function getConfig(params) {
  return request({
    url: '/demo/vueFile',
    method: 'get',
    params
  })
}
