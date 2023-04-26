<template>
  <div class="app-container">
  <el-row>
    <el-col :span="8">
      <el-input size="small" v-model="searchField" @change="fetchData" prefix-icon="el-icon-search" placeholder="地址 | 名称 | 业务标识"></el-input>
    </el-col>
    <el-col :span="16">
      <el-row class="float-right">
        <el-button size="small" icon="el-icon-plus">创建集群</el-button>
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
      <el-table-column label="服务地址" align="center">
        <template slot-scope="scope">
          {{ scope.row.domain }}
        </template>
      </el-table-column>
      <el-table-column label="集群名称" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.clusterName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="所属业务" align="center">
        <template slot-scope="scope">
          {{ scope.row.bizKey }}
        </template>
      </el-table-column>
      <el-table-column label="服务实例数" align="center">
        <template slot-scope="scope">
          {{ scope.row.instanceCount }}
        </template>
      </el-table-column>
      <el-table-column label="服务规格" align="center">
        <template slot-scope="scope">
          {{ scope.row.useQuota }}
        </template>
      </el-table-column>
      <el-table-column label="是否共用配置中心" align="center">
        <template slot-scope="scope">
          {{ scope.row.configShare | configShareFilter}}
        </template>
      </el-table-column>
      <el-table-column label="配置中心地址" align="center">
        <template slot-scope="scope">
          {{ scope.row.configUrl }}
        </template>
      </el-table-column>
      <el-table-column label="配置中心账密" align="center">
        <template slot-scope="scope">
          {{ scope.row.configUser }}:{{ scope.row.configSecret }}
        </template>
      </el-table-column>
      <el-table-column label="其他配置" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="showOtherConfig(scope.row)">点击查看</el-button>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center">
        <template slot-scope="scope">
          {{ scope.row.meno }}
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="集群状态" align="center">
        <template slot-scope="scope">
          <el-tag effect="dark" :type="scope.row.status | statusFilter">{{ scope.row.status | statusNameFilter}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="edit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" @click="del(scope.row)">删除</el-button>
          <el-button size="mini" type="text" @click="enable(scope.row)" v-if="scope.row.status === 'PENDING' || scope.row.status === 'FAILED'">启用</el-button>
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
  import { pageList } from '@/api/cluster'
  import { getClusterStatusTagType,getClusterStatusName } from '@/utils/status'
  export default {
    name: "CusterManage",
    data(){
      return {
        searchField: null,
        multipleSelection: [],
        pager: {
          pageSizes: [10, 20, 50, 100],
          pageSize: 10,
          currentPage: 1,
          totalCount: 0
        },
        listLoading: false,
        list: [],
      }
    },
    filters: {
      statusFilter(status) {
        return getClusterStatusTagType(status)
      },
      statusNameFilter(status){
        return getClusterStatusName(status)
      },
      configShareFilter(configShare){
        return configShare ? "是":"否"
      }
    },
    created() {
      this.fetchData()
    },
    methods: {
      handleSelectionChange(val){
        this.multipleSelection = val;
      },
      fetchData() {
        this.listLoading = true;
        let params = {pageSize: this.pager.pageSize, currentPage: this.pager.currentPage};
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
      showOtherConfig(rowData){
        console.log(configItem)
      }
    }
  }
</script>

<style scoped>

</style>
