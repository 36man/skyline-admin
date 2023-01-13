<template>
  <div class="app-container">
    <el-row>
      <el-col :span="8">
        <el-input size="small" v-model="searchField" @change="fetchData" prefix-icon="el-icon-search" placeholder="插件 | 作者"></el-input>
      </el-col>
      <el-col :span="16">
        <el-row class="float-right">
          <el-button size="small" icon="el-icon-upload2">上传</el-button>
          <el-button size="small" icon="el-icon-delete">删除</el-button>
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
        <el-table-column type="selection" width="50" align="center">
        </el-table-column>
        <el-table-column align="center" label="序号" width="55">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="插件名称">
          <template slot-scope="scope">
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column label="作者" width="110" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.author }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最新版本" width="110" align="center">
          <template slot-scope="scope">
            {{ scope.row.ver }}
          </template>
        </el-table-column>
        <el-table-column label="使用次数" width="110" align="center">
          <template slot-scope="scope">
            {{ scope.row.useCount }}
          </template>
        </el-table-column>
        <el-table-column class-name="status-col" label="使用状态" width="110" align="center">
          <template slot-scope="scope">
            <el-tag effect="dark" :type="scope.row.inUse | statusFilter">{{ scope.row.inUse === 'yes' ? '使用中' : '未使用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="created_at" label="更新时间" width="200">
          <template slot-scope="scope">
            <i class="el-icon-time" />
            <span>{{ scope.row.updateTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="showVersionList(scope.row)">版本管理</el-button>
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
    <plugin-version v-model="pluginVersionEnable" :plugin-id="pluginId"></plugin-version>
  </div>
</template>

<script>
  import { getList } from '@/api/plugin'
  import PluginVersion from "./version";
	export default {
		name: "PluginManage",
    components: {PluginVersion},
    data(){
		  return {
        searchField: null,
        multipleSelection: [],
        pager: {
          pageSizes: [20, 50, 100, 200],
          pageSize: 20,
          currentPage: 1,
          total: 0
        },
        listLoading: false,
        list: [],
        pluginVersionEnable: false,
        pluginId: null,
      }
    },
    filters: {
      statusFilter(status) {
        const statusMap = {
          yes: 'danger',
          no: 'success',
        }
        return statusMap[status]
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
        getList(params).then(response => {
          context.list = response.data.items
          context.pager.total = response.data.total;
          context.listLoading = false
        })
      },
      showVersionList(rowData) {
        this.pluginVersionEnable = true;
        this.pluginId = rowData.id;
      }
    }
	}
</script>

<style scoped>

</style>
