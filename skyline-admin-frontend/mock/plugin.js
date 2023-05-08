const constants = require('./mock-constants.js')

const Mock = require('mockjs')

const data = Mock.mock({
  'items|10': [{
    id: '1',
    maintainer: '@sentence(5, 8)',
    classDefine: 'Test.class',
    pluginName: '测试插件',
    overview: '插件描述',
    createTime: '@datetime',
    updateTime: '@datetime',
  }]
})

const data2 = Mock.mock({
  'items|10': [{
    id: '1',
    ver: 'v@integer(1, 5).@integer(0, 3).@integer(1, 50)',
    features: ["高效","高效","高效"],
    pluginVO: data.items[0],
    pageContent: constants.templateContent,
    typeMeta: '{"capableSwitchList": [{"age": 10, "name": "张三"},{"birthday": 1682510430723, "height": 175}],"perpetualResourceList": [{"a": "aaa", "b": "bbbb"}]}',
    'active|1': [true, false],
    size: '@integer(1000000,1000000000)',
    jarUrl: 'file://xxx/xxx/xxx.jar',
    fileKey: 'abc',
    createTime: '@datetime',
    updateTime: '@datetime',
  }]
})

module.exports = [
  {
    url: '/plugin/pageList',
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
    url: '/plugin/upload',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/1',
    type: 'delete',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/enable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/disable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/ver/pageList',
    type: 'get',
    response: config => {
      const items = data2.items
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
    url: '/plugin/ver/search',
    type: 'get',
    response: config => {
      const items = data2.items
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
    url: '/plugin/ver/enable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/ver/disable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
]
