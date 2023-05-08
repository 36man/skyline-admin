const constants = require('./mock-constants.js')
const Mock = require('mockjs')
const data = Mock.mock({
  pageContent: constants.templateContent
})
module.exports = [
  {
    url: '/demo/vueFile',
    type: 'get',
    response: config => {
      return {
        code: 200,
        data: data
      }
    }
  }
]
