<template>
  <el-dialog title="版本列表" :visible.sync="dialogDisplay">
    <el-row>
      <el-table
        v-loading="listLoading"
        :data="list"
        element-loading-text="Loading"
        border
        fit
        highlight-current-row
      >
        <el-table-column align="center" label="序号" >
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="版本号" align="center">
          <template slot-scope="scope">
            {{ scope.row.ver }}
          </template>
        </el-table-column>
        <el-table-column class-name="status-col" label="状态" align="center">
          <template slot-scope="scope">
            <el-tag effect="dark" :type="scope.row.status | statusFilter">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="created_at" label="更新时间">
          <template slot-scope="scope">
            <i class="el-icon-time" />
            <span>{{ scope.row.updateTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="success" @click="changeStatus(scope.row)" v-if="scope.row.status === 'off'">启用</el-button>
            <el-button size="mini" type="danger" @click="changeStatus(scope.row)" v-if="scope.row.status === 'on'">禁用</el-button>
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
        :total="pager.total">
      </el-pagination>
    </el-row>
  </el-dialog>
</template>

<script>
  import { verPageList, verSearch, verEnableById, verDisableById } from '@/api/plugin'
  export default {
    name: "PluginVersion",
    props: {
      value: {
        type: Boolean,
        default: false
      },
      pluginId: {
        type: String,
        default: ''
      }
    },
    data(){
      return {
        pager: {
          pageSizes: [10, 20, 50],
          pageSize: 10,
          currentPage: 1,
          total: 0
        },
        listLoading: false,
        list: [],
        dialogDisplay: false,
      }
    },
    filters: {
      statusFilter(status) {
        const statusMap = {
          on: 'success',
          off: 'danger',
        }
        return statusMap[status]
      },
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
      pluginId() {
        this.fetchData()
      },
    },
    methods: {
      fetchData() {
        this.listLoading = true;
        let params = {pageSize: this.pager.pageSize, currentPage: this.pager.currentPage, pluginId: this.pluginId};
        let context = this;
        getList(params).then(response => {
          context.list = response.data.items
          context.pager.total = response.data.total;
          context.listLoading = false
        })
      },
      changeStatus(data) {
        this.$message.info("修改状态为：" + !data.status);
      }
    }
  }
</script>

<style scoped>

</style>
