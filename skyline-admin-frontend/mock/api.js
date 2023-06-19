const Mock = require('mockjs')
const plugins = '[' +
  '{"stage": "stage_1", "stageName": "阶段一", "stateSn": 1, "sn": 1, "configParams": {"name": "张三", "age": 1}, "pluginVerId": "1"},' +
  '{"stage": "stage_1", "stageName": "阶段一", "stateSn": 1, "sn": 2, "configParams": {"aaa": "李四", "bbb": 1}, "pluginVerId": "2"},' +
  '{"stage": "stage_2", "stageName": "阶段二", "stateSn": 4, "sn": 2, "configParams": {"ccc": "王五", "ddd": 1}, "pluginVerId": "1"},' +
  '{"stage": "stage_2", "stageName": "阶段二", "stateSn": 4, "sn": 1, "configParams": {"eee": "赵六", "fff": 1}, "pluginVerId": "2"},' +
  '{"stage": "stage_3", "stageName": "阶段三", "stateSn": 3, "sn": 1, "configParams": {"ggg": "黄芪", "hhh": 1}, "pluginVerId": "3"}' +
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
