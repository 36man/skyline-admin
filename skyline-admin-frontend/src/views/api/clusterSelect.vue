/* eslint-disable */
<template>
  <el-dialog title="选择网关" :visible.sync="dialogDisplay">
    <el-row>
      <el-col :span="16">
        <div style="display: flex; padding-right: 5px;">
          <el-input size="small" v-model="domain" @change="fetchData" placeholder="地址"></el-input>
          <el-input size="small" v-model="bizKey" @change="fetchData" placeholder="名称"></el-input>
          <el-input size="small" v-model="clusterName" @change="fetchData" placeholder="业务标识"></el-input>
        </div>
      </el-col>
    </el-row>
    <el-row class="margin-top-10px">
      <el-table
        v-loading="listLoading"
        :data="list"
        @row-dblclick="selectRow"
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
  </el-dialog>
</template>

<script>
  import { pageList } from '@/api/cluster'
  import { getClusterStatusTagType, getClusterStatusName } from '@/utils/status'
	export default {
		name: "clusterSelect",
    props: {
      value: {
        type: Boolean,
        default: false
      },
    },
    data(){
		  return {
        dialogDisplay: false,
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
      }
    },
    watch: {
      dialogDisplay(newValue){
        if(!newValue){
          this.$emit('input', newValue)
        }
      },
      value(newValue){
        if(newValue) {
          this.dialogDisplay = true;
        }
      },
    },
    filters: {
      statusFilter(status) {
        return getClusterStatusTagType(status)
      },
      statusNameFilter(status){
        return getClusterStatusName(status)
      },
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
      selectRow(row){
        this.dialogDisplay = false;
        this.$emit('select', row)
      }
    }
	}
</script>

<style scoped>

</style>
