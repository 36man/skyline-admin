import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/plugin/list',
    method: 'get',
    params
  })
}
