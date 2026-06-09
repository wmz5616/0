import { post } from '@/utils/request';
import { urlName } from '@/utils/utils';
import { UploadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { history } from '@umijs/max';
import {
  Alert,
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Modal,
  Radio,
  Row,
  Select,
  Spin,
  Table,
  Upload,
} from 'antd';
import moment from 'moment';
import React from 'react';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;

class Index extends React.Component {
  formRef = React.createRef();
  formRefz = React.createRef();
  state = {
    spinning: false,
    list: [],
    selectedRowKeys: [],
    reviewVisible: false,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    shopList: [],
    circleList: [],
    modalType: 'audit',
    auitResult: 1,
    record: {},
    fileList: [],
  };

  componentDidMount() {
    this.getData();
    this.getAdminList();
    this.getShopList();
  }

  getAdminList = async () => {
    const res = await post(`/guzhe/common/supermarket/lists`, {
      searchIntStatus: 1,
    });
    if (res && res.code == 10000) {
      this.setState({
        circleList: res.data || [],
      });
    } else {
      message.error(res?.msg);
    }
  };

  getShopList = async () => {
    const res = await post(`/guzhe/common/shop/lists`, {
      psearchIntStatus: 1,
    });
    if (res && res.code == 10000) {
      this.setState({
        shopList: res.data || [],
      });
    } else {
      message.error(res?.msg);
    }
  };

  getData = () => {
    // 商品订单
    this.setState(
      {
        spinning: true,
      },
      async () => {
        //列表
        const res = await post(`/guzhe/screen_order/lists`, {
          orderUserKeyword: this.state.keyword,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          orderNo: this.state.orderNo,
          serialNumber: this.state.deviceNo,
          merchantName: this.state.shopName,
          businessCircleName: this.state.businessCircleName,
          pageNum: this.state.pageNum,
          status: this.state.status,
          pageSize: 10,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          const data = res.data || {};
          const pageInfo = data.pageInfo || {};
          this.setState({
            orderInfo: data,
            list: pageInfo.list || [],
            total: pageInfo.total,
          });
        } else {
          message.error(res?.msg);
        }
      },
    );
  };

  handleOk = () => {
    this.formRefz.current.validateFields().then(async (values) => {
      const res = await post(
        this.state.modalType == 'audit'
          ? `/guzhe/screen_order/audit`
          : `/guzhe/screen_order/cancel`,
        {
          orderId: this.state.record.orderId,
          result: this.state.auitResult || undefined,
          [this.state.modalType == 'audit' ? 'remark' : 'cancelReason']:
            values.auditOpinion,
          fileUrl:
            this.state.fileList
              .map((file) => file.response?.data?.url)
              .join(',') || undefined,
        },
      );

      if (res && res.code == 10000) {
        message.success(res.msg);
        this.formRefz.current.resetFields();
        this.setState(
          {
            reviewVisible: false,
            fileList: [],
          },
          () => {
            this.getData();
          },
        );
      } else {
        message.error(res?.msg);
      }
    });
  };

  handleUploadChange =
    (type) =>
      ({ file, fileList }) => {
        // 处理文件删除的情况
        if (file.status === 'removed') {
          this.setState({ [type]: fileList });
          return;
        }
        this.setState({ [type]: fileList }, () => {
          const { response = {} } = file;
          if (response.code === 10000) {
            const data = this.state[type];
            if (data[data.length - 1]) {
              console.log(data[data.length - 1]);
              data[data.length - 1].response.data.url =
                urlName + data[data.length - 1].response.data.url;
              this.setState({
                [type]: data,
              });
            } else {
              this.setState({
                [type]: [],
              });
            }
          } else {
            if (response.msg) {
              message.info(response.msg);
            }
          }
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

  onFinish = (vas) => {
    this.setState(
      {
        keyword: vas.keyword,
        startTime: vas.payTime
          ? vas.payTime[0].format('YYYY-MM-DD')
          : undefined,
        endTime: vas.payTime ? vas.payTime[1].format('YYYY-MM-DD') : undefined,
        orderNo: vas.orderNo,
        deviceNo: vas.deviceNo,
        businessCircleName: vas.businessCircleName,
        shopName: vas.shopName,
        status: vas.status,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        orderNo: undefined,
        deviceNo: undefined,
        shopName: undefined,
        status: undefined,
        businessCircleName: undefined,
        selectedRowKeys: [],
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };
  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  showModal = (type, record) => {
    const auitResult = type == 'cancel' ? 0 : 1;
    this.setState(
      {
        modalType: type,
        reviewVisible: true,
        auitResult,
        record,
      },
      () => {
        this.formRefz.current.setFieldsValue({
          auitResult,
        });
      },
    );
  };

  Export = () => {
    let xhr = new XMLHttpRequest();
    let fileName = `店位订单${moment().format('YYYY-MM-DD HH:mm:ss')}.xls`; // 文件名称
    const that = this;
    xhr.open('POST', `/guzhe/screen_order/export`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.responseType = 'blob';
    xhr.setRequestHeader('token', localStorage.getItem('token')); // 请求头中的验证信息等（如果有）

    xhr.onload = function () {
      if (this.status == 200) {
        let type = xhr.getResponseHeader('Content-Type');

        if (type.includes('application/json')) {
          const fileReader = new FileReader();
          fileReader.onload = function () {
            const res = JSON.parse(this.result);
            message.error(res?.msg || '导出失败');
          };
          fileReader.readAsText(this.response);
          return;
        }

        let blob = new Blob([this.response], { type: type });
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
        } else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(blob);
          if (fileName) {
            var a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download == 'undefined') {
              window.location = objectUrl;
            } else {
              a.href = objectUrl;
              a.download = fileName;
              document.body.appendChild(a);
              a.click();
              a.remove();
              that.setState({
                loading: false,
              });
            }
          } else {
            window.location = objectUrl;
          }
          URL.revokeObjectURL(objectUrl);
        }
      }
    };
    xhr.send(
      JSON.stringify({
        orderUserKeyword: this.state.keyword,
        startTime: this.state.startTime,
        endTime: this.state.endTime,
        orderNo: this.state.orderNo,
        serialNumber: this.state.deviceNo,
        merchantName: this.state.shopName,
        businessCircleName: this.state.businessCircleName,
        status: this.state.status,
      }),
    );
  };

  render() {
    const { selectedRowKeys, total } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
    const columns = [
      {
        title: 'ID',
        dataIndex: 'orderId',
      },
      {
        title: '支付时间',
        dataIndex: 'orderTime',
      },
      {
        title: '设备编号',
        dataIndex: 'serialNumber',
      },
      {
        title: '所属商超',
        dataIndex: 'businessCircleName',
      },
      {
        title: '下单商家',
        dataIndex: 'merchantName',
      },
      {
        title: '订单金额',
        dataIndex: 'totalAmountText',
      },
      {
        title: '租用月份',
        dataIndex: 'rentalMonths',
      },
      {
        title: '下单人',
        dataIndex: 'orderUserText',
      },
      {
        title: '订单号',
        dataIndex: 'orderNo',
      },
      {
        title: '订单状态',
        dataIndex: 'status',
             fixed: 'right',
        render: (text) => (
          <div
            style={{
              color:
                text == 0
                  ? '#2e99ff'
                  : text == 1
                    ? '#f79a71'
                    : text == 2
                      ? 'red'
                      : text == 3
                        ? '#27b43e '
                        : text == 4
                          ? '#8b8b8b'
                          : text == 5
                            ? '#8b8b8b'
                            : '',
            }}
          >
            {text == 0
              ? '待确认'
              : text == 1
                ? '待生效'
                : text == 2
                  ? '生效中'
                  : text == 3
                    ? '已完成'
                    : text == 4
                      ? '已驳回'
                      : text == 5
                        ? '已撤销'
                        : ''}
          </div>
        ),
      },
      {
        title: '操作',
        fixed: 'right',
        dataIndex: '',
        render: (text, record) => {
          return (
            <>
              {record.status == 0 && (
                <span
                  className="clickFont"
                  style={{ marginLeft: 10 }}
                  onClick={() => this.showModal('audit', record)}
                >
                  审核
                </span>
              )}
              {(record.status == 1 ||
                record.status == 2 ||
                record.status == 3) && (
                  <span
                    className="clickFont"
                    style={{ marginLeft: 10, color: 'red' }}
                    onClick={() => this.showModal('cancel', record)}
                  >
                    撤销
                  </span>
                )}
              <span
                className="clickFont"
                style={{ marginLeft: 10 }}
                onClick={() =>
                  history.push(
                    `/storefrontRentalManagement/orderDetails?id=${record.orderId}`,
                  )
                }
              >
                详情
              </span>
            </>
          );
        },
      },
    ];

    return (
      <PageContainer
        header={{
          title: ``,
        }}
      >
        <Spin spinning={this.state.spinning}>
          <div
            style={{
              backgroundColor: '#fff',
              padding: '20px 20px 0 20px',
              marginBottom: 15,
            }}
          >
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="支付时间" name="payTime">
                    <RangePicker />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="设备编号" name="deviceNo">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="所属商超" name="businessCircleName">
                    <Select
                      showSearch
                      placeholder="请选择"
                      optionFilterProp="children"
                    >
                      {this.state.circleList.map((sa) => (
                        <Option value={sa.name}>{sa.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="下单商家" name="shopName">
                    <Select allowClear placeholder="请选择">
                      {this.state.shopList.map((res) => (
                        <Option value={res.name}>{res.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="下单人" name="keyword">
                    <Input placeholder="请输入姓名或手机号" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="订单号" name="orderNo">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="订单状态" name="status">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>待确认</Option>
                      <Option value={1}>待生效</Option>
                      <Option value={2}>生效中</Option>
                      <Option value={3}>已完成</Option>
                      <Option value={4}>已驳回</Option>
                      <Option value={5}>已撤销</Option>
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
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row gutter={16}>
              <Col span={6}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  店位订单管理
                </h1>
              </Col>
              <Col
                span={18}
                style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}
              >
                <Button variant="outlined" onClick={this.Export}>
                  导出
                </Button>
              </Col>
              <Col span={24} style={{ margin: '10px 0' }}>
                <Alert
                  showIcon
                  message={`订单总数量：${this.state.orderInfo?.orderCount || 0
                    } | 订单总金额：${this.state.orderInfo?.totalAmountText / 100 || 0
                    } 元`}
                ></Alert>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              // rowSelection={rowSelection}
               rowSelection={false}
              rowKey="orderId"
              columns={columns}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: this.state.pageSize,
                total,
                current: this.state.pageNum,
              }}
              scroll={{ x: 'max-content' }}
            />
          </div>
          <Modal
            title={this.state.modalType == 'audit' ? '审核' : '撤销'}
            visible={this.state.reviewVisible}
            onOk={this.handleOk}
            onCancel={() => {
              this.setState({ reviewVisible: false, fileList: [] });
              this.formRefz.current.resetFields();
            }}
          >
            <Form ref={this.formRefz} wrapperCol={16} labelWrap={8}>
              {this.state.modalType == 'audit' && (
                <Form.Item
                  name="auitResult"
                  label="审核意见"
                  rules={[{ required: true, message: '请输入' }]}
                >
                  <Radio.Group
                    onChange={(e) => {
                      this.setState({
                        auitResult: e.target.value,
                      });
                    }}
                  >
                    <Radio value={1}>确认</Radio>
                    <Radio value={2}>驳回</Radio>
                  </Radio.Group>
                </Form.Item>
              )}
              <Form.Item
                name="files"
                label={this.state.auitResult == 1 ? '支付凭证' : '文件'}
              >
                <Upload
                  action="/guzhe/file/upload"
                  fileList={this.state.fileList}
                  onChange={this.handleUploadChange('fileList')}
                  beforeUpload={(file) => {
                    const allowedTypes = [
                      'image/jpeg',
                      'image/png',
                      'image/jpg',
                      'application/pdf',
                      'application/msword',
                      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                      'application/vnd.ms-excel',
                      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                      'text/plain',
                    ];
                    const allowedExtensions = [
                      '.jpg',
                      '.png',
                      '.jpeg',
                      '.pdf',
                      '.doc',
                      '.docx',
                      '.xlsx',
                      '.xls',
                      '.txt',
                    ];
                    const fileExtension = file.name
                      .substring(file.name.lastIndexOf('.'))
                      .toLowerCase();

                    const isAllowedType =
                      allowedTypes.includes(file.type) ||
                      allowedExtensions.includes(fileExtension);
                    if (!isAllowedType) {
                      message.error(
                        '不支持的文件类型，请上传 .jpg .png .jpeg .pdf .doc .docx .xlsx .xls .txt 格式的文件',
                      );
                      return Upload.LIST_IGNORE;
                    }

                    return true;
                  }}
                  headers={{ token: localStorage.getItem('token') }}
                >
                  <Button icon={<UploadOutlined />}>上传文件</Button>
                  <div
                    style={{
                      position: 'relative',
                      color: 'rgba(0, 0, 0, 0.427450980392157)',
                    }}
                  >
                    <span>
                      支持扩展名：.jpg .png .jpeg .pdf .doc .docx .xlsx .xls
                      .txt
                    </span>
                  </div>
                </Upload>
              </Form.Item>
              <Form.Item
                label={
                  this.state.modalType == 'cancel'
                    ? '撤销原因'
                    : this.state.auitResult == 1
                      ? '审核意见'
                      : '驳回原因'
                }
                name="auditOpinion"
                rules={[
                  { required: this.state.modalType == 'cancel' || this.state.auitResult == 2, message: '请输入' },
                ]}
              >
                <TextArea rows={4} placeholder="请输入" />
              </Form.Item>
            </Form>
          </Modal>
        </Spin>
      </PageContainer>
    );
  }
}

export default Index;
