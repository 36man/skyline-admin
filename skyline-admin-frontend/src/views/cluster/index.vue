<template>
  <div class="app-container">
    <el-row>
      <el-col :span="16">
        <div style="display: flex; padding-right: 5px;">
          <el-input size="small" v-model="domain" @change="fetchData" placeholder="地址"></el-input>
          <el-input size="small" v-model="bizKey" @change="fetchData" placeholder="名称"></el-input>
          <el-input size="small" v-model="clusterName" @change="fetchData" placeholder="业务标识"></el-input>
        </div>
      </el-col>
      <el-col :span="8">
        <el-row class="float-right">
          <el-button size="small" icon="el-icon-plus" @click="showCreate">创建集群</el-button>
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
      >
        <el-table-column align="center" label="序号" width="55">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
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
        <el-table-column label="服务地址" align="center">
          <template slot-scope="scope">
            {{ scope.row.domain }}
          </template>
        </el-table-column>
        <el-table-column label="服务实例数" align="center" width="65">
          <template slot-scope="scope">
            {{ scope.row.instanceCount }}
          </template>
        </el-table-column>
        <el-table-column label="服务规格" align="center">
          <template slot-scope="scope">
            {{ scope.row.useQuota }}
          </template>
        </el-table-column>
        <el-table-column label="共用配置中心" align="center" width="65">
          <template slot-scope="scope">
            {{ scope.row.configShare | configShareFilter}}
          </template>
        </el-table-column>
        <el-table-column label="配置中心地址" align="center">
          <template slot-scope="scope">
            {{ scope.row.configUrl }}
          </template>
        </el-table-column>
        <el-table-column label="配置中心账密" align="center" width="160">
          <template slot-scope="scope">
            <el-row style="text-align: left;">
              <el-tag type="success">账号</el-tag> : <el-tag>{{ scope.row.configUser }}</el-tag>
            </el-row>
            <el-row style="text-align: left;">
              <el-tag type="success">密码</el-tag> : <el-tag>{{ scope.row.configSecret }}</el-tag>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column label="其他配置" align="center" width="250">
          <template slot-scope="scope">
            <el-row v-for="(p) in parseConfig(scope.row.configItem)" style="text-align: left;">
              <el-tag type="success">{{p.key}}</el-tag> : <el-tag>{{p.value}}</el-tag>
            </el-row>
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
        <el-table-column label="操作" align="center" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="showEdit(scope.row)" v-if="scope.row.status === 'PENDING'">编辑</el-button>
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
    <cluster-form :id="clusterId" v-model="clusterFormVisible" @submit="fetchData"></cluster-form>
  </div>
</template>

<script>
  import { pageList, deleteById, applyCluster } from '@/api/cluster'
  import { getClusterStatusTagType,getClusterStatusName } from '@/utils/status'
  import { parseObj2KVArray } from '@/utils/jsons'
  import ClusterForm from "./clusterForm";
  export default {
    name: "CusterManage",
    components: {ClusterForm},
    data(){
      return {
        domain: '',
        bizKey: '',
        clusterName: '',
        pager: {
          pageSizes: [10, 20, 50, 100],
          pageSize: 10,
          currentPage: 1,
          totalCount: 0
        },
        listLoading: false,
        list: [],
        clusterFormVisible: false,
        clusterId: null,
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
      fetchData() {
        this.listLoading = true;
        let params = {
          pageSize: this.pager.pageSize,
          pageNo: this.pager.currentPage,
          domain: this.domain,
          bizKey: this.bizKey,
          clusterName: this.clusterName
        };
        let context = this;
        pageList(params).then(response => {
          context.list = response.data.data
          context.pager.totalCount = response.data.totalCount;
          context.listLoading = false
        })
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
      enable(rowData){
        let context = this;
        this.$confirm("启用后集群可被选择，确定要启用吗？").then(() => {
          applyCluster(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      showCreate(){
        this.clusterFormVisible = true;
        this.clusterId = '';
      },
      showEdit(rowData) {
        this.clusterFormVisible = true;
        this.clusterId = rowData.id;
      },
      parseConfig(config){
        return parseObj2KVArray(config);
      }
    }
  }
</script>

<style scoped>

</style>
