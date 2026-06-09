import { getToken } from '@/utils/authority';
import { post } from '@/utils/request';
import { UploadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Row,
  Select,
  Spin,
  Table,
  Upload,
} from 'antd';
import React from 'react';
import AddShoppModal from './AddShoppModal';
// 将connect导入
const { RangePicker } = DatePicker;
// 报名管理
const { Option } = Select;
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};
const layouts = {
  labelCol: { span: 7 },
  wrapperCol: { span: 15 },
};
class productManagement extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefss = React.createRef();
  ticketFormRef = React.createRef();

  state = {
    NewType: false,
    spinning: false,
    loading: false,
    bLoading: false,
    imageUrl: '',
    pageNum: 1,
    selectedRowKeys: [],
    majorList: [],
    adminList: [],
    ticketFileList: [],
  };

  componentDidMount() {
    const { id, type } = this.props
    this.setState({
      shopId: id,
      type
    }, () => {
      if (id) {
        this.getData();
        this.getGymList();
        this.getAdminList(1);
      }
    })
  }

  getAdminList = async (e) => {
    const res = await post('/guzhe/user/lists', {
      pageSize: 30,
      pageNum: e,
    });
    if (res && res.code == 10000) {
      this.setState(
        {
          adminList: this.state.adminList.concat(res.data.list),
        },
        () => {
          if (res.data.total > this.state.adminList.length) {
            this.getAdminList((e += 1));
          }
        },
      );
    } else {
      message.error(res?.msg);
    }
  };

  getGymList = async () => {
    const res = await post('/guzhe/product/category/lists', {
      searchField4: +this.state.shopId,
    });
    if (res && res.code == 10000) {
      this.setState({
        majorList: res.data,
      });
    } else {
      message.error(res?.msg);
    }
  };

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      async () => {
        const params = {
          shopId: +this.state.shopId,
          name: this.state.name,
          pageNum: this.state.pageNum,
          productNo: this.state.productNo,
          stockStatus: this.state.stockStatus,
          categoryId: this.state.categoryId,
          status: this.state.status,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
        };
        const res = await post('/guzhe/product/lists', params);
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          console.log(res.data);
          this.setState({
            gymTypelist: res.data.list,
            total: res.data.total,
          });
        } else {
          message.error(res?.msg);
        }
      },
    );
  };

  showModal = (add, id) => {
    this.setState({
      NewType: true,
      add: add,
      addUrl: undefined,
      imageUrl: undefined,
      id,
    });
  };

  handleCancel = () => {
    this.setState({
      NewType: false,
    });
  };

  handleOk = () => {
    this.setState({
      NewType: false,
    });
  };

  //查询
  onFinish = (vas) => {
    this.setState(
      {
        productNo: vas.productNo,
        name: vas.name,
        stockStatus: vas.stockStatus,
        categoryId: vas.categoryId,
        status: vas.status,
        startTime: vas.time
          ? vas.time[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTime: vas.time
          ? vas.time[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  //重置
  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        productNo: undefined,
        name: undefined,
        stockStatus: undefined,
        categoryId: undefined,
        status: undefined,
        startTime: undefined,
        endTime: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  //删除
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的数据');
    } else {
      const res = await post('/guzhe/product/delete', {
        deleteIds: ids,
      });
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  updateContent = (value, index) => {
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

  showStroageModal = (e) => {
    this.setState(
      {
        stroageVisible: true,
        shotName: e.name,
        id: e.id,
      },
      () => {
        setTimeout(() => {
          this.formRefs.current.setFieldsValue({
            stock: e.stock,
          });
        }, 300);
      },
    );
  };

  changeStroageSubmit = () => {
    this.formRefs.current.validateFields().then(async (values) => {
      const res = await post('/guzhe/product/updateStock', {
        productId: this.state.id,
        stock: values.stock,
      });
      if (res.code == 10000) {
        message.success(res.msg);
        this.setState({ stroageVisible: false });
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
  };

  showTickModal = (e) => {
    this.setState({
      id: e,
      tickVisible: true,
    });
  };

  downloadTicker = () => {
    let xhr = new XMLHttpRequest();
    let fileName = `查看劵码.xls`; // 文件名称
    const that = this;
    xhr.open('POST', `/guzhe/product/ticket/export`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.responseType = 'blob';
    xhr.setRequestHeader('token', getToken()); // 请求头中的验证信息等（如果有）
    this.setState({ bLoading: true });

    xhr.onload = function () {
      that.setState({
        bLoading: false,
      });
      if (this.status == 200) {
        let type = xhr.getResponseHeader('Content-Type');

        let blob = new Blob([this.response], { type: type });
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
        } else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(blob);
          if (fileName) {
            let a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download === 'undefined') {
              window.location = objectUrl;
            } else {
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
    let data = { searchId: this.state.id };
    xhr.send(JSON.stringify(data)); // 发送请求体（如果有）
  };

  downloadFile = () => {
    const fileName = '券码导入模板.xls';
    const filePath = '/quanMa.xlsx';

    const link = document.createElement('a');
    link.href = filePath;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  export = () => {
    let xhr = new XMLHttpRequest();
    let fileName = `商品列表.xls`; // 文件名称
    const that = this;
    xhr.open('POST', `/guzhe/product/export`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.responseType = 'blob';
    xhr.setRequestHeader('token', localStorage.getItem('token')); // 请求头中的验证信息等（如果有）

    xhr.onload = function () {
      if (this.status == 200) {
        let type = xhr.getResponseHeader('Content-Type');

        let blob = new Blob([this.response], { type: type });
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
        } else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(blob);
          if (fileName) {
            let a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download === 'undefined') {
              window.location = objectUrl;
            } else {
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
    let data = {
      name: this.state.name,
      productNo: this.state.productNo,
      stockStatus: this.state.stockStatus,
      categoryId: this.state.categoryId,
      status: this.state.status,
      startTime: this.state.startTime,
      endTime: this.state.endTime,
    };
    xhr.send(JSON.stringify(data)); // 发送请求体（如果有）
  };

  render() {
    const { gymTypelist = [], total, selectedRowKeys } = this.state;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '商品编号',
        dataIndex: 'productNo',
        render: (text, record) => {
          return (
            <div>
              {text}

              {record.is_top ? (
                <span
                  style={{
                    padding: '1px 3px',
                    backgroundColor: '#ddeeff',
                    color: '#1890ff',
                    borderRadius: '3px',
                    border: ' 1px solid #a7d5ff',
                    marginLeft: 5,
                  }}
                >
                  顶
                </span>
              ) : (
                ''
              )}
            </div>
          );
        },
      },
      {
        title: '图片',
        dataIndex: 'coverImage',
        render: (text, record) => {
          return (
            <>
              <img
                src={text}
                alt=""
                style={{ width: 103.5, height: 37.5, objectFit: 'contain' }}
              />
            </>
          );
        },
      },
      {
        title: '商品名称',
        dataIndex: 'name',
      },
      {
        title: '商品规格',
        dataIndex: 'specification',
      },
      {
        title: '单位',
        dataIndex: 'unit',
      },
      {
        title: '售价（元）',
        dataIndex: 'price',
        render: (text) => <div>{(text / 100).toFixed(2)}</div>,
      },
      {
        title: '商品分类',
        dataIndex: 'categoryList',
        render: (res) => <div>{res && res.map((x) => x.name).join('、')}</div>,
      },
      {
        title: '上架状态',
        dataIndex: 'status',
        render: (x) => (
          <div
            style={{
              color:
                x == 2
                  ? 'rgba(245, 154, 35, 0.84)'
                  : 'rgba(17, 175, 33, 0.847)',
            }}
          >
            {x == 1 ? '上架' : x == 2 ? '下架' : '定时上架'}
          </div>
        ),
      },
      {
        title: '商品库存',
        dataIndex: 'stock',
      },
      {
        title: '虚拟商品',
        dataIndex: 'isVirtual',
        render: (x) => (
          <div
            style={{
              color:
                x == 1
                  ? 'rgba(17, 175, 33, 0.847)'
                  : 'rgba(245, 154, 35, 0.84)',
            }}
          >
            {x == 1 ? '是' : '否'}
          </div>
        ),
      },
      {
        title: '销量',
        dataIndex: 'sales'
      },
      {
        title: '排序',
        dataIndex: 'sort',
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
      },
      {
        title: '操作',
        width: 200,
        fixed: 'right',
        render: (text, record) => {
          return (
            <div>
              <span
                className="clickFont"
                onClick={() => this.showModal(false, record.id)}
              >
                编辑
              </span>
              {record.isVirtual == 1 && (
                <span
                  className="clickFont"
                  onClick={() => this.showTickModal(record.id)}
                  style={{ paddingLeft: 8 }}
                >
                  添加劵码
                </span>
              )}
              {record.isVirtual != 1 && (
                <span
                  className="clickFont"
                  style={{ paddingLeft: 10 }}
                  onClick={() => this.showStroageModal(record)}
                >
                  更改库存
                </span>
              )}
              <Popconfirm
                title={
                  <>
                    <div>删除提示</div>
                    <div>
                      <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                      <span style={{ color: '#ccc' }}>确定删除吗？</span>
                    </div>
                  </>
                }
                onConfirm={() => this.deletes([record.id])}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
              >
                <span className="mL15 red">删除</span>
              </Popconfirm>
            </div>
          );
        },
      },
    ];

    return (
      <Spin spinning={this.state.spinning}>
          <div
            style={{
              backgroundColor: '#fff',
              padding: '20px 20px 0 0',
              marginBottom: 15,
            }}
          >
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="商品编号" name="productNo">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="商品名称" name="name">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="商品分类" name="categoryId">
                    <Select
                      showSearch
                      placeholder="请选择"
                      optionFilterProp="children"
                    >
                      {this.state.majorList.map((sa) => (
                        <Option value={sa.id}>{sa.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="上架状态" name="status">
                    <Select showSearch placeholder="请选择">
                      <Option value={1}>上架</Option>
                      <Option value={2}>下架</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="创建时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="库存数量" name="stockStatus">
                    <Select showSearch placeholder="请选择">
                      <Option value={1}>有库存</Option>
                      <Option value={0}>无库存</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col
                  className="gutter-row"
                  style={{ textAlign: 'right', flex: '1 0 220px' }}
                >
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

          <div
            style={{
              backgroundColor: '#fff',
              padding: 20,
              minHeight: window.innerHeight - 280,
            }}
          >
            <Row>
              <Col span={12}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  {this.props.shopName}-商品管理
                </h1>
              </Col>
              <Col span={12} style={{ textAlign: 'right' }}>
                <Button
                  type="primary"
                  onClick={() => this.showModal(true)}
                  style={{ marginRight: 15 }}
                >
                  +添加商品
                </Button>
                <Button onClick={this.export} style={{ marginRight: 15 }}>
                  导出
                </Button>
                <Popconfirm
                  title={
                    <>
                      <div>删除提示</div>
                      <div>
                        <span style={{ color: 'red' }}>删除的内容不可恢复</span>
                        ，<span style={{ color: '#ccc' }}>确定删除吗？</span>
                      </div>
                    </>
                  }
                  onConfirm={() => this.deletes(selectedRowKeys)}
                  // onCancel={cancel}
                  okText="是"
                  cancelText="否"
                >
                  <Button danger>删除</Button>
                </Popconfirm>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              columns={columns}
              className="csdivcenter"
              rowKey="id"
              rowSelection={rowSelection}
              dataSource={gymTypelist}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: this.state.pageNum,
              }}
              scroll={{ x: 'max-content' }}
            />
          </div>
          {this.state.NewType && (
            <AddShoppModal
              handleOk={this.handleOk}
              getData={this.getData}
              add={this.state.add}
              gymList={this.state.gymList}
              id={this.state.id}
              adminList={this.state.adminList}
              shopId={this.state.shopId}
            />
          )}
          <Modal
            maskClosable={false}
            title={`更改库存-${this.state.shotName}`}
            open={this.state.stroageVisible}
            onOk={this.changeStroageSubmit}
            onCancel={() => this.setState({ stroageVisible: false })}
          >
            <Form ref={this.formRefs} {...layout}>
              <Form.Item
                label="商品库存"
                name="stock"
                rules={[{ required: false }]}
              >
                <InputNumber min={0} rows={3} placeholder="请输入" />
              </Form.Item>
            </Form>
          </Modal>
          <Modal
            title={`券码`}
            open={this.state.tickVisible}
            onOk={(e) => {
              this.ticketFormRef.current.resetFields();
              this.setState({ tickVisible: false });
            }}
            onCancel={(e) => {
              this.ticketFormRef.current.resetFields();
              this.setState({ tickVisible: false });
            }}
          >
            <Form ref={this.ticketFormRef} {...layouts}>
              <Form.Item name="Import template" label="导入模板">
                <div style={{ position: 'relative' }}>
                  <a className="clickFont" onClick={this.downloadFile}>
                    点击下载导入模板
                  </a>
                  <br />
                  <span
                    style={{
                      color: 'rgba(0, 0, 0, 0.427450980392157)',
                      position: 'absolute',
                    }}
                  >
                    请按照模板要求导入
                  </span>
                </div>
              </Form.Item>

              <Form.Item
                name="files"
                label="导入文件"
                rules={[
                  {
                    required: true,
                    message: '请上传',
                  },
                ]}
              >
                <Upload
                  action="/guzhe/product/ticket/import"
                  data={{ productId: this.state.id }}
                  fileList={this.state.ticketFileList}
                  onChange={(e) => {
                    this.setState({ ticketFileList: e.fileList });
                    if (e.file.status == 'done') {
                      const { response } = e.file;
                      if (response.code == 10000) {
                        this.ticketFormRef.current.resetFields();
                        this.setState({ tickVisible: false, ticketFileList: [] });
                        message.success(response.msg);
                        this.getData();
                      } else {
                        const data = response.data || [];
                        message.error(`${response.msg}${data.length ? `：${data.join('、')}` : ''}`);
                      }
                    }
                  }}
                  headers={{ token: localStorage.getItem('token') }}
                >
                  <Button icon={<UploadOutlined />}>上传文件</Button>
                </Upload>
              </Form.Item>
              <div
                style={{
                  position: 'relative',
                  color: 'rgba(0, 0, 0, 0.427450980392157)',
                  left: 120,
                }}
              >
                <span>支持扩展名：.xls .xlsx</span>
              </div>
              <Form.Item
                name="files"
                label="查看已上传劵码"
                rules={[
                  {
                    required: true,
                    message: '请上传',
                  },
                ]}
              >
                <Button onClick={this.downloadTicker} icon={<UploadOutlined />} loading={this.state.bLoading}>
                  下载文件
                </Button>
              </Form.Item>
            </Form>
          </Modal>
      </Spin>
    );
  }
}

export default productManagement;
