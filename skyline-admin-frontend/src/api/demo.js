import request from '@/utils/request'

export function getPageContent() {
  return request({
    url: '/demo/vueFile',
    method: 'get',
  })
}
