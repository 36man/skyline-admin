const Mock = require('mockjs')

const data = Mock.mock({
  'items|10': [{
    id: '1',
    matchCondition: '匹配规则',
    description: '描述信息',
    'status|1': ['enable', 'disable', 'in_enable', 'new'],
    meno: '备注',
    plugins: '[{"stage": "", "stageName": "", "stateSn": 1, "jarUrl": "file://xxxx.jar", "sn": 1, "config": "插件页配置后的数据", "classDefine": "Test.class", "ver": "1.0.1"}]',
    createTime: '@datetime',
    updateTime: '@datetime',
    clusterVO: {id: 'cluster-id1', clusterName: '测试集群1'}
  }]
})

module.exports = [
  {
    url: '/api/pageList',
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
    url: '/api',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/api/1',
    type: 'put',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/api/1',
    type: 'delete',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/api/enable/1',
    type: 'put',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/api/disable/1',
    type: 'put',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
]
