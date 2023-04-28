/* eslint-disable */
<template>
  <div class="app-container">
    <el-row>
      <el-col :span="16">
        <div style="display: flex; padding-right: 5px;">
          <el-input size="small" v-model="pluginName" @change="fetchData" placeholder="名称"></el-input>
          <el-input size="small" v-model="maintainer" @change="fetchData" placeholder="作者"></el-input>
          <el-input size="small" v-model="classDefine" @change="fetchData" placeholder="标识"></el-input>
        </div>
      </el-col>
      <el-col :span="8">
        <el-row class="float-right">
          <el-button size="small" icon="el-icon-upload2" @click="showUpload">上传插件</el-button>
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
        <el-table-column label="插件标识" align="center">
          <template slot-scope="scope">
            {{ scope.row.classDefine }}
          </template>
        </el-table-column>
        <el-table-column label="插件名称" align="center">
          <template slot-scope="scope">
            {{ scope.row.pluginName }}
          </template>
        </el-table-column>
        <el-table-column label="插件作者" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.maintainer }}</span>
          </template>
        </el-table-column>
        <el-table-column label="插件描述" align="center">
          <template slot-scope="scope">
            {{ scope.row.overview }}
          </template>
        </el-table-column>
        <el-table-column align="center" prop="created_at" label="更新时间" width="180">
          <template slot-scope="scope">
            <i class="el-icon-time" />
            <span>{{ scope.row.updateTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="版本" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="showVersion(scope.row)">点击查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="showEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="del(scope.row)">删除</el-button>
            <el-button size="mini" type="text" @click="enable(scope.row)" v-if="scope.row.status === 'off'">启用</el-button>
            <el-button size="mini" type="text" @click="disable(scope.row)" v-if="scope.row.status === 'off'">禁用</el-button>
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
    <!-- version list -->
    <plugin-version v-model="pluginVersionVisible" :plugin-id="pluginId"></plugin-version>
    <!-- form -->
    <plugin-info-form v-model="pluginInfoFormVisible" :row-data="pluginInfoFormData" @submit="fetchData" />
    <!-- upload -->
    <plugin-upload v-model="pluginUploadVisible" />
  </div>
</template>

<script>
  import { pageList, deleteById, enableById, disableById} from '@/api/plugin'
  import PluginVersion from "./pluginVersion";
  import PluginUpload from "./pluginUpload";
  import PluginInfoForm from "./pluginInfoForm";
	export default {
		name: "PluginManage",
    components: {PluginVersion, PluginUpload, PluginInfoForm},
    data(){
		  return {
        pluginName: null,
        maintainer: null,
        classDefine: null,
        multipleSelection: [],
        pager: {
          pageSizes: [10, 20, 50, 100],
          pageSize: 10,
          currentPage: 1,
          totalCount: 0
        },
        listLoading: false,
        list: [],
        pluginVersionVisible: false,
        pluginId: null,
        pluginInfoFormVisible: false,
        pluginInfoFormData: {},
        pluginUploadVisible: false,
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
        let params = {
          pageSize: this.pager.pageSize,
          pageNo: this.pager.currentPage,
          pluginName: this.pluginName,
          classDefine: this.classDefine,
          maintainer: this.maintainer,
        };
        let context = this;
        pageList(params).then(response => {
          context.list = response.data.data
          context.pager.totalCount = response.data.totalCount;
          context.listLoading = false
        })
      },
      showVersion(rowData) {
        this.pluginVersionVisible = true;
        this.pluginId = rowData.id;
      },
      del(rowData){
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
        this.$confirm("启用后插件可被选择，确定要启用吗？").then(() => {
          enableById(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      disable(rowData){
        let context = this;
        this.$confirm("禁用后插件不可被选择，确定要禁用吗？").then(() => {
          disableById(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      showEdit(rowData){
        this.pluginInfoFormVisible = true;
        this.pluginInfoFormData = rowData;
      },
      showUpload(){
        this.pluginUploadVisible = true;
      }
    }
	}
</script>

<style scoped>

</style>
