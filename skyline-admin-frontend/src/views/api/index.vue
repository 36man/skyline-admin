/* eslint-disable */
<template>
  <div class="app-container">
    <el-row>
      <el-col :span="8">
        <div style="display: flex;">
          <el-input size="small" v-model="matchCondition" @change="fetchData" prefix-icon="el-icon-search" placeholder="匹配规则"></el-input>
        </div>
      </el-col>
      <el-col :span="16">
        <el-row class="float-right">
          <el-tag type="success">当前集群：{{currentCluster.clusterName}}</el-tag>
          <el-button size="small" icon="el-icon-s-help">选择集群</el-button>
          <el-button size="small" icon="el-icon-plus">创建api</el-button>
        </el-row>
      </el-col>
    </el-row>
    <el-row class="margin-top-10px">
      <el-table
        v-loading="listLoading"
        :data="list"
        element-loading-text="Loading"
        border
        fit
        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <el-table-column align="center" label="序号" width="55">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="匹配规则" align="center">
          <template slot-scope="scope">
            {{ scope.row.matchCondition }}
          </template>
        </el-table-column>
        <el-table-column label="api描述" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center">
          <template slot-scope="scope">
            {{ scope.row.meno }}
          </template>
        </el-table-column>
        <el-table-column class-name="status-col" label="状态" align="center">
          <template slot-scope="scope">
            <el-tag effect="dark" :type="scope.row.status | statusFilter">{{ scope.row.status | statusNameFilter}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="edit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="del(scope.row)">删除</el-button>
            <el-button size="mini" type="text" @click="showConfigPluginChain(scope.row)">配置插件链</el-button>
            <el-button size="mini" type="text" @click="enable(scope.row)" v-if="scope.row.status === 'disable' || scope.row.status === 'new'">启用</el-button>
            <el-button size="mini" type="text" @click="disable(scope.row)" v-if="scope.row.status === 'enable'">禁用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
    <el-row class="margin-top-10px">
      <el-pagination
        @size-change="fetchData"
        @current-change="fetchData"
        :current-page="pager.currentPage"
        :page-sizes="pager.pageSizes"
        :page-size="pager.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        background
        hide-on-single-page
        :total="pager.totalCount">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
  import { pageList } from '@/api/api'
  import { getApiStatusName,getApiStatusTagType } from '@/utils/status'
  export default {
    name: "ApiManage",
    data() {
      return {
        clusterId: null,
        //是否查询集群信息
        isClusterLoad: false,
        matchCondition: null,
        multipleSelection: [],
        pager: {
          pageSizes: [10, 20, 50, 100],
          pageSize: 10,
          currentPage: 1,
          totalCount: 0
        },
        currentCluster: {
          id: '1',
          clusterName: '集群名称',
          domain: 'www.baidu.com',
          bizKey: '@sentence(1, 3)',
          instanceCount: '@integer(1, 2)',
        },
        listLoading: false,
        list: [],
      }
    },
    filters: {
      statusFilter(status) {
        return getApiStatusTagType(status)
      },
      statusNameFilter(status) {
        return getApiStatusName(status)
      },
    },
    created() {
      this.fetchData()
    },
    methods: {
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      fetchData() {
        this.listLoading = true;
        let params = {pageSize: this.pager.pageSize, pageNo: this.pager.currentPage, matchCondition: this.matchCondition, clusterId: this.clusterId, isClusterLoad: this.isClusterLoad};
        let context = this;
        pageList(params).then(response => {
          context.list = response.data.data
          context.pager.totalCount = response.data.totalCount;
          context.listLoading = false
        })
      },
      edit(rowData) {
        console.log(rowData)
      },
      del(rowData) {
        console.log(rowData)
      },
      enable(rowData){

      },
      disable(rowData){

      },
      showConfigPluginChain(rowData){
        console.log(rowData.plugins)
      }
    }
  }
</script>

<style scoped>

</style>
