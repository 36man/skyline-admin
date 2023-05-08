const Mock = require('mockjs')
const constants = require('./mock-constants.js')
const plugins = '[' +
  '{"stage": "stage_1", "stageName": "阶段一", "stateSn": 1, "jarUrl": "file://xxxx.jar", "sn": 1, "config": "' + constants.templateContent + '", "classDefine": "Test1.class", "ver": "1.0.1"},' +
  '{"stage": "stage_1", "stageName": "阶段一", "stateSn": 1, "jarUrl": "file://xxxx.jar", "sn": 2, "config": "' + constants.templateContent + '", "classDefine": "Test1.class", "ver": "1.0.2"},' +
  '{"stage": "stage_2", "stageName": "阶段二", "stateSn": 4, "jarUrl": "file://xxxx.jar", "sn": 2, "config": "' + constants.templateContent + '", "classDefine": "Test2.class", "ver": "2.0.1"},' +
  '{"stage": "stage_2", "stageName": "阶段二", "stateSn": 4, "jarUrl": "file://xxxx.jar", "sn": 1, "config": "' + constants.templateContent + '", "classDefine": "Test2.class", "ver": "2.0.2"},' +
  '{"stage": "stage_3", "stageName": "阶段三", "stateSn": 3, "jarUrl": "file://xxxx.jar", "sn": 1, "config": "' + constants.templateContent + '", "classDefine": "Test3.class", "ver": "3.0.1"}' +
  ']'

const data = Mock.mock({
  'items|10': [{
    id: '1',
    matchCondition: '匹配规则',
    description: '描述信息',
    'status|1': ['enable', 'disable', 'in_enable', 'new'],
    meno: '备注',
    plugins: plugins,
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
