const Mock = require('mockjs')

const data = Mock.mock({
  'items|10': [{
    id: '@id',
    name: '@sentence(5, 15)',
    'inUse|1': ['yes', 'no'],
    author: 'name',
    updateTime: '@datetime',
    useCount: '@integer(300, 5000)',
    ver: 'v@integer(1, 9).@integer(1, 9).@integer(1, 9)',
  }]
})

const data2 = Mock.mock({
  'items|10': [{
    id: '@id',
    'status|1': ['on', 'off'],
    updateTime: '@datetime',
    ver: 'v@integer(1, 9).@integer(1, 9).@integer(1, 9)',
  }]
})

module.exports = [
  {
    url: '/plugin/list',
    type: 'get',
    response: config => {
      const items = data.items
      return {
        code: 20000,
        data: {
          total: 21,
          items: items
        }
      }
    }
  },
  {
    url: '/plugin/version/list',
    type: 'get',
    response: config => {
      const items = data2.items
      return {
        code: 20000,
        data: {
          total: 21,
          items: items
        }
      }
    }
  },
]
