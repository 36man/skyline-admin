const Mock = require('mockjs')

const data = Mock.mock({
  'items|10': [{
    id: '1',
    clusterName: '@sentence(5, 15)',
    domain: 'www.baidu.com',
    bizKey: 'test-cluster',
    instanceCount: '@integer(1, 2)',
    'configShare|1': [true, false],
    configUrl: 'nacos://192.168.0.110:8080',
    configUser: 'nacos',
    configSecret: 'nacos123',
    useQuota: '4c8g',
    'status|1': ['PENDING', 'RUNNING', 'SUCCEEDED', 'FAILED', 'UNKNOWN'],
    statusName: '创建成功,未开始调度',
    meno: '备注',
    configItem: {

    },
  }]
})

module.exports = [
  {
    url: '/cluster/pageList',
    type: 'get',
    response: config => {
      const items = data.items
      return {
        code: 200,
        data: {
          totalCount: 21,
          data: items
        }
      }
    }
  },
  {
    url: '/cluster/1',
    type: 'get',
    response: config => {
      const items = data.items
      return {
        code: 200,
        data: items[0]
      }
    }
  },
  {
    url: '/cluster',
    type: 'post',
    response: config => {
      const items = data.items
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/cluster/1',
    type: 'put',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/cluster/1',
    type: 'delete',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/cluster/applyCluster/1',
    type: 'put',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
]