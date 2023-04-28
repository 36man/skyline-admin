/* eslint-disable */
<template>
  <el-dialog width="65%" title="版本列表" :visible.sync="dialogDisplay">
    <el-row>
      <el-table
        v-loading="listLoading"
        :data="list"
        element-loading-text="Loading"
        border
        fit
        highlight-current-row
      >
        <el-table-column align="center" label="序号">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="版本号" align="center">
          <template slot-scope="scope">
            {{ scope.row.ver }}
          </template>
        </el-table-column>
        <el-table-column label="特性" align="center">
          <template slot-scope="scope">
            {{ scope.row.features }}
          </template>
        </el-table-column>
        <el-table-column label="jar地址" align="center">
          <template slot-scope="scope">
            {{ scope.row.jarUrl }}
          </template>
        </el-table-column>
        <el-table-column label="jar大小" align="center">
          <template slot-scope="scope">
            {{ scope.row.size }}
          </template>
        </el-table-column>
        <el-table-column class-name="status-col" label="状态" align="center">
          <template slot-scope="scope">
            <el-tag effect="dark" :type="scope.row.active | statusFilter">{{ scope.row.active ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="created_at" label="更新时间" width="180">
          <template slot-scope="scope">
            <i class="el-icon-time" />
            <span>{{ scope.row.updateTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="能力开关" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="showTypeData(scope.row)">点击查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="UI预览" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="showConfigPage(scope.row)">点击预览</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.active === false" size="mini" type="text" @click="enable(scope.row)">启用</el-button>
            <el-button v-if="scope.row.active === true" size="mini" type="text" @click="disable(scope.row)">禁用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
    <el-row class="margin-top-10px">
      <el-pagination
        :current-page="pager.currentPage"
        :page-sizes="pager.pageSizes"
        :page-size="pager.pageSize"
        :total="pager.totalCount"
        layout="total, sizes, prev, pager, next, jumper"
        background
        hide-on-single-page
        @size-change="fetchData"
        @current-change="fetchData">
      </el-pagination>
    </el-row>
    <!-- configItem dialog -->
    <el-dialog width="35%" append-to-body title="能力开关" :visible.sync="typeMetaDialogVisible">
      <b-code-editor ref="editor" v-model="typeMetaStr" theme="material" :lint="true" :show-number="true" :readonly="true" :indent-unit="4" :line-wrap="false" />
    </el-dialog>
    <!-- pageContent dialog -->
    <el-dialog width="45%" append-to-body title="UI预览" :visible.sync="pageContentDialogVisible">
      <display ref="display" :code="pageContentStr" />
    </el-dialog>
  </el-dialog>
</template>

<script>
  import { verPageList, verEnableById, verDisableById } from '@/api/plugin'
  import Display from '@/components/DynamicDisplay/display';
  export default {
    name: "PluginVersion",
    components: { Display },
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
          totalCount: 0
        },
        listLoading: false,
        list: [],
        dialogDisplay: false,
        typeMetaDialogVisible: false,
        typeMetaStr: '{}',
        pageContentDialogVisible: false,
        pageContentStr: '',
      }
    },
    filters: {
      statusFilter(status) {
        const statusMap = {
          true: 'success',
          false: 'info',
        }
        return statusMap[status]
      },
    },
    watch: {
      dialogDisplay(newValue){
        if (!newValue){
          this.$emit('input', newValue)
        }
      },
      value(newValue){
        if (newValue) {
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
        let params = {pageSize: this.pager.pageSize, pageNo: this.pager.currentPage, pluginId: this.pluginId};
        let context = this;
        verPageList(params).then(response => {
          context.list = response.data.data
          context.pager.totalCount = response.data.totalCount;
          context.listLoading = false
        })
      },
      enable(rowData){
        let context = this;
        this.$confirm("启用后插件可被选择，确定要启用吗？").then(() => {
          verEnableById(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      disable(rowData){
        let context = this;
        this.$confirm("禁用后插件不可被选择，确定要禁用吗？").then(() => {
          verDisableById(rowData.id).then(() => {
            context.$message.success("操作成功")
            context.fetchData()
          })
        }).catch(() => {})
      },
      showTypeData(rowData){
        this.typeMetaDialogVisible = true;
        this.typeMetaStr = rowData.typeMeta;
        this.$nextTick(() => {
          this.$refs["editor"].formatCode()
        })
      },
      showConfigPage(rowData){
        this.pageContentDialogVisible = true;
        this.pageContentStr = rowData.pageContent;
      }
    }
  }
</script>

<style scoped>

</style>
