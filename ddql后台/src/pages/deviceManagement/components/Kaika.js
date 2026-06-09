import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Select,
  Input,
  Table,
  Row,
  Col,
  Form,
  message,
  Popconfirm,
  Spin,
  Alert,
  Modal,
  Upload,
  Radio,
  InputNumber,
  DatePicker,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
const { RangePicker } = DatePicker;
import CKEditor from 'react-ckeditor-wrapper';
import update from 'immutability-helper';
import { getToken } from '@/utils/authority';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { handleExport } from '@/utils/utils';
// 将connect导入
import { connect } from 'umi';
import moment from 'moment';

// 应用类型
const { Option } = Select;
const { Search } = Input;
const { TextArea } = Input;

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};

class DataConnection extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefss = React.createRef();
  state = {
    NewType: false,
    RecommendedSettings: false,
    confirmLoading: false,
    spinning: false,
    loading: false,
    pageSize: 10,
    isModalVisible: false,
    pageNum: 1,
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        //列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            pageNum: this.state.pageNum,
            pageSize: this.state.pageSize,
            keyword: this.state.keyword,
            searchStrField1: this.state.searchStrField1,
            searchIntStatus: this.state.searchIntStatus,
            beginTime: this.state.beginTime,
            endTime: this.state.endTime,
          },
          url: `/ddql/vipCard/selectOrder`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              this.setState({
                spinning: false,
                gymTypelist: res.data.list,
                total: res.data.total,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  // sort_data:[
  //   {
  //     id:dragRow.id,
  //     sort:hoverIndex
  //   }
  // ]

  // 添加设置
  showModal = (record) => {
    if (record) {
      this.setState({
        isModalVisible: true,
      });
    }
  };
  handleOk = () => {
    this.formRef.current.validateFields().then((values) => {
      //编辑
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {},
        // dataName: 'developerListData',
        method: 'POST',
        url: `/api/admin/feedback/process`,
        myData: (res) => {
          if (res.code === 200) {
            message.success(res.message);
            this.setState({
              isModalVisible: false,
            });
            this.getData();
          }
        },
      });
    });
  };

  handleCancel = () => {
    this.setState({
      isModalVisible: false,
    });
  };

  beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') == -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
  };

  handleChange = (info) => {
    if (info.file.status === 'uploading') {
      this.setState({ loading: true });
      return;
    }
    if (info.file.status === 'done') {
      console.log(info);
      this.setState({
        addUrl: info.file.response.data.uri,
      });

      message.success({ content: '上传成功', duration: 0.7 });
      // Get this url from response in real world.
      this.getBase64(info.file.originFileObj, (imageUrl) =>
        this.setState({
          imageUrl,
          uditUrl: imageUrl,
          loading: false,
        }),
      );
    }
  };

  siteFrontReserve = (v, ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的类型');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          enable: v,
          id: ids.join(','),
        },
        url: `/api/admin/article/enable`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            message.success(res.message);
            this.getData();
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要操作的类型');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/article/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            message.success(res.message);
            this.getData();
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  //退款
  refund = (e) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        orderNo: e.orderNo,
        refundPrice: e.orderPrice,
        refundRemark: '退款',
      },
      url: `/ddql/order/refund`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
      },
      () => {
        this.getData();
      },
    );
  };

  //搜索
  onFinish = (vas) => {
    console.log(vas);
    this.setState(
      {
        keyword: vas.keyword,
        searchStrField1: vas.searchStrField1,
        searchIntStatus: vas.searchIntStatus,
        beginTime: vas.time ? moment(vas.time[0]).format('YYYY-MM-DD 00:00:00') : undefined,
        endTime: vas.time ? moment(vas.time[1]).format('YYYY-MM-DD 23:59:59') : undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  export = () => {
    const that = this;
    let xhr = new XMLHttpRequest();
    let fileName = `会员开卡记录.xls`; // 文件名称
    const params = {
      keyword: this.state.keyword,
      searchStrField1: this.state.searchStrField1,
      searchIntStatus: this.state.searchIntStatus,
      beginTime: this.state.beginTime,
      endTime: this.state.endTime,
    };
    xhr.open('GET', `/ddql/vipCard/order/export?${handleExport(params)}`, true);
    xhr.responseType = 'blob';
    xhr.setRequestHeader('Content-Type', 'application/json');
    const data = { searchIds: this.state.selectedRowKeys };

    xhr.setRequestHeader('token', localStorage.getItem('token')); // 请求头中的验证信息等（如果有）
    xhr.onload = function () {
      if (this.status === 200) {
        let type = xhr.getResponseHeader('Content-Type');
        let blob = new Blob([this.response], { type: type });
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
        } else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(blob);
          if (fileName) {
            var a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download === 'undefined') {
              window.location = objectUrl;
            } else {
              console.log(objectUrl);
              a.href = objectUrl;
              a.download = fileName;
              document.body.appendChild(a);
              a.click();
              a.remove();
            }
          } else {
            window.location = objectUrl;
          }
        }
      }
    };
    xhr.send(JSON.stringify(data));
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        keyword: undefined,
        searchStrField1: undefined,
        searchIntStatus: undefined,
        beginTime: undefined,
        endTime: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  render() {
    const {
      gymTypelist = [],
      loading,
      add,
      NewType,
      imageUrl,
      selectedRowKeys,
      list = [],
    } = this.state;
    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',

        onChange: (info) => {
          const fileType = [
            'doc',
            'txt',
            'pdf',
            'zip',
            'rar',
            'xls',
            'xlsx',
            'docs',
            'pptx',
            'ppt',
          ];
          if (info.file.status === 'done') {
            console.log(info.file);
            const url = info.file.response.data.uri;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };

    const columns = [
      {
        title: '序号',
        dataIndex: 'id',
      },

      {
        title: '昵称',
        dataIndex: 'vipCardName',
      },
      {
        title: '手机号码',
        dataIndex: 'userPhone',
      },
      {
        title: '会员卡名称',
        dataIndex: 'vipCardName',
      },
      {
        title: '会员编号',
        dataIndex: 'vipNo',
      },
      {
        title: '有效期',
        dataIndex: 'year',
        render: (res) => <div>1年</div>,
      },
      {
        title: '开卡时间',
        dataIndex: 'cardOpenTime',
      },
      {
        title: '有效期至',
        dataIndex: 'cardExpireTime',
        render: (res) => <div>{res ? res : '永久'}</div>,
      },
      {
        title: '金额（元）',
        dataIndex: 'orderPrice',
        render: (res) => <div>{(res / 100).toFixed(2)}</div>,
      },
      {
        title: '卡状态',
        dataIndex: 'status',
        render: (res) => (
          <div
            style={{
              color:
                res == 0
                  ? '#FBBA13'
                  : res == 1
                  ? 'rgba(3, 191, 22, 0.64)'
                  : res == 2
                  ? 'F5222D'
                  : res == 3
                  ? 'rgb(204, 204, 204)'
                  : '#F5222D',
            }}
          >
            {res == 0
              ? '待支付'
              : res == 1
              ? '正常'
              : res == 2
              ? '已退款'
              : res == 3
              ? '已取消'
              : '已用完'}
          </div>
        ),
      },
      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
              <Popconfirm
                title={
                  <>
                    <div>退款提示</div>
                    <div>
                      <span style={{ color: '#ccc' }}>是否确认退款？</span>
                    </div>
                  </>
                }
                onConfirm={() => this.refund(record)}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
              >
                <a className="red">退款</a>
              </Popconfirm>
            </div>
          );
        },
      },
    ];

    return (
      <>
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 20 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={4} style={{ marginRight: 20 }}>
                  <Form.Item label="关键词" name="keyword">
                    <Input placeholder="请输入昵称或手机号码" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4} style={{ marginRight: 20 }}>
                  <Form.Item label="会员卡名称" name="searchStrField1">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="开卡时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4} style={{ marginRight: 20 }}>
                  <Form.Item label="卡状态" name="searchIntStatus">
                    <Select placeholder="请选择">
                      <Option value={0}>未支付</Option>
                      <Option value={1}>正常</Option>
                      <Option value={2}>已退款</Option>
                      <Option value={3}>已取消</Option>
                      <Option value={4}>已到期</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" style={{ flex: '1 0 220px', textAlign: 'right' }}>
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>

                    <Button className="mL15" onClick={this.resets}>
                      重置
                    </Button>
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
        </Spin>
        <div style={{ backgroundColor: '#fff', padding: 20 }}>
          <Row>
            <Col span={6}>
              {' '}
              <h1 style={{ fontWeight: '600', fontSize: '18px' }}>会员开卡记录</h1>
            </Col>
            <Col span={18}>
              <div style={{ textAlign: 'right' }}>
                <Button loading={this.state.loading} className="mL15" onClick={this.export}>
                  导出
                </Button>
              </div>
            </Col>
          </Row>

          <Table
            columns={columns}
            rowKey="id"
            rowSelection={rowSelection}
            dataSource={gymTypelist}
            components={this.components}
            pagination={{
              showSizeChanger: false,
              onChange: this.pageChange,
              pageSize: this.state.pageSize,
              total: this.state.total,
              current: this.state.pageNum,
            }}
          />
        </div>

        {/* </div>
        </div> */}
      </>
    );
  }
}

// 7
// 绑定到本页面，此处就可以拿到请求的值，
// allModels所有models集合，mapping是具体的model的命名空间，mappingData命名空间里面你想取的值
export default connect((allModels) => ({}))(DataConnection);
