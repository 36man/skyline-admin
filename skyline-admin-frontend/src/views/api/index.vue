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
          <el-button size="small" icon="el-icon-s-help" @click="clusterSelectVisible = true">选择集群</el-button>
          <el-button size="small" icon="el-icon-plus" @click="showCreateApi">创建api</el-button>
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
            <el-button size="mini" type="text" v-if="scope.row.status === 'disable' || scope.row.status === 'new'" @click="edit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" v-if="scope.row.status === 'disable' || scope.row.status === 'new'" @click="del(scope.row)">删除</el-button>
            <el-button size="mini" type="text" v-if="scope.row.status === 'disable' || scope.row.status === 'new'" @click="showConfigPluginChain(scope.row)">配置插件链</el-button>
            <el-button size="mini" type="text" v-if="scope.row.status === 'disable' || scope.row.status === 'new'" @click="enable(scope.row)">启用</el-button>
            <el-button size="mini" type="text" v-if="scope.row.status === 'in_enable' || scope.row.status === 'enable'" @click="disable(scope.row)">禁用</el-button>
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

    <!-- cluster select -->
    <cluster-select v-model="clusterSelectVisible" @select="onSelectRow"/>
    <!-- api form -->
    <api-form v-model="apiFormVisible" :api-data="apiFormData" @submit="fetchData"></api-form>
    <!-- plugin chain form -->
    <plugin-chain-form v-model="pluginChainFormVisible" :api-data="pluginChainFormData" @submit="fetchData"></plugin-chain-form>
  </div>
</template>

<script>
  import { pageList, deleteById, enableById, disableById } from '@/api/api'
  import { getApiStatusName,getApiStatusTagType } from '@/utils/status'
  import ClusterSelect from './clusterSelect'
  import ApiForm from './apiForm'
  import PluginChainForm from './pluginChainForm'
  export default {
    name: "ApiManage",
    components: { ClusterSelect, ApiForm, PluginChainForm },
    data() {
      return {
        clusterId: null,
        //是否查询集群信息，此处需要查，否则没有clusterId
        isClusterLoad: true,
        matchCondition: null,
        multipleSelection: [],
        pager: {
          pageSizes: [10, 20, 50, 100],
          pageSize: 10,
          currentPage: 1,
          totalCount: 0
        },
        clusterSelectVisible: false,
        currentCluster: {},
        listLoading: false,
        list: [],
        apiFormVisible: false,
        apiFormData: {},
        pluginChainFormVisible: false,
        pluginChainFormData: {}
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
      enable(rowData){
        let context = this;
        this.$confirm("启用后api可被访问，确定要启用吗？").then(() => {
          enableById(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      disable(rowData){
        let context = this;
        this.$confirm("禁用后api不可被访问，确定要禁用吗？").then(() => {
          disableById(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      del(rowData) {
        let context = this;
        this.$confirm("删除后不可恢复，确定要删除吗？").then(() => {
          deleteById(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      showCreateApi(){
        if(!this.clusterId){
          this.$message.warning("请先选择集群")
        } else {
          this.apiFormVisible = true;
          this.apiFormData = {
            clusterId: this.clusterId,
            matchCondition: '',
            description: '',
            meno: '',
          }
        }
      },
      edit(rowData) {
        this.apiFormVisible = true;
        this.apiFormData = {
          id: rowData.id,
          clusterId: rowData.clusterVO.clusterId,
          matchCondition: rowData.matchCondition,
          description: rowData.description,
          meno: rowData.meno,
        }
      },
      onSelectRow(row){
        this.currentCluster = row;
        this.clusterId = row.id;
        this.fetchData()
      },
      showConfigPluginChain(rowData){
        this.pluginChainFormVisible = true;
        this.pluginChainFormData = {
          id: rowData.id,
          pluginList: rowData.plugins
        }
      }
    }
  }
</script>

<style scoped>

</style>
