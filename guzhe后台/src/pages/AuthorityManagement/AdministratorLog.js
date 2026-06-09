import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Modal,
  Row,
  Spin,
  Table,
  Tabs,
} from 'antd';
import React from 'react';
const { TabPane } = Tabs;
const { RangePicker } = DatePicker;
//管理员日志
class AdministratorLog extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    details: false,
    ss: 1,
    pageNum: 1,
    pageNums: 1,
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      async () => {
        const res = await post(`/guzhe/log/login/lists`, {
          keyword: this.state.keyword,
          ip: this.state.ip,
          phone: this.state.phone,
          endTime: this.state.endTime,
          pageNum: this.state.pageNum,
          startTime: this.state.startTime,
          description: this.state.description,
          pageSize: 10,
          type: 1,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          this.setState({
            list: res.data.list,
            total: res.data.total,
          });
        } else {
          message.error(res?.msg);
        }

        const ress = await post(`/guzhe/log/operate/lists`, {
          keyword: this.state.keyword,
          ip: this.state.ip,
          phone: this.state.phone,
          endTime: this.state.endTime,
          pageNum: this.state.pageNums,
          startTime: this.state.startTime,
          description: this.state.description,
          pageSize: 10,
          type: 2,
        });
        this.setState({
          spinning: false,
        });
        if (ress && ress.code === 10000) {
          this.setState({
            lists: ress.data.list,
            totals: ress.data.total,
          });
        } else {
          message.error(ress?.msg);
        }
      },
    );
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({ selectedRowKeys });
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

  pageChanges = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNums: page,
      },
      () => {
        this.getData();
      },
    );
  };

  showModal = (record) => {
    this.setState({
      details: true,
      record,
    });
  };

  handleOk = () => {
    this.setState({
      details: false,
    });
  };

  // 删除函数
  deletes = (ids) => {
    console.log(ids);
  };

  onFinish = (vae) => {
    console.log(vae);
    this.setState(
      {
        keyword: vae.keyword,
        ip: vae.ip,
        phone: vae.phone && vae.phone.replace(/\s+/g, ''),
        startTime: vae.time
          ? vae.time[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTime: vae.time
          ? vae.time[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
        description: vae.description,
        ip: vae.ip,
        pageNum: 1,
        pageNums: 1,
      },
      () => {
        console.log(this.state);
        this.getData();
      },
    );
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        keyword: undefined,
        ip: undefined,
        phone: undefined,
        endTime: undefined,
        startTime: undefined,
        description: undefined,
        pageNum: 1,
        pageNums: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  //日期
  onChange = (value, dateString) => {
    console.log('Formatted Selected Time: ', dateString);

    this.setState(
      {
        time_arr: dateString[0] !== '' && [
          `${dateString[0]} 00:00`,
          `${dateString[1]} 23:59`,
        ],
      },
      () => {
        // this.getData()
      },
    );
  };

  callback = (key) => {
    console.log(key);
    this.setState(
      {
        ss: key,
      },
      () => {
        this.resets();
      },
    );
  };

  render() {
    const {
      list = [],
      selectedRowKeys,
      lists = [],
      record,
      pageNum,
      pageNums,
      total,
      totals,
      ss,
    } = this.state;

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
        title: '操作人',
        dataIndex: 'admin',
        render: (text, record) => {
          return (
            <div>
              {record.name}（{record.account}）
            </div>
          );
        },
      },
      {
        title: 'ip',
        dataIndex: 'ip',
      },
      {
        title: '时间',
        dataIndex: 'loginTime',
      },
    ];

    const columnss = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '操作人',
        dataIndex: 'action_name',
        render: (text, record) => {
          return (
            <div>
              {record.name}（{record.account}）
            </div>
          );
        },
      },

      {
        title: '操作名称',
        dataIndex: 'description',
        render: (text, record) => {
          // const xx=JSON.parse(text)
          // console.log(xx)
          return <div>{record.description}</div>;
        },
      },
      {
        title: '访问地址',
        dataIndex: 'api',
      },
      {
        title: '访问ip',
        dataIndex: 'ip',
      },
      {
        title: '操作时间',
        dataIndex: 'operateTime',
      },
      {
        title: '操作',
        dataIndex: 'extractType',
        render: (text, record) => {
          return <a onClick={() => this.showModal(record)}>详情</a>;
        },
      },
    ];

    return (
      <div className="zxcv">
        <div className="asd">
          <PageContainer
            header={{
              title: ``,
            }}
          >
            <Spin spinning={this.state.spinning}>
              <div
                style={{
                  backgroundColor: '#fff',
                  padding: 24,
                  paddingBottom: 4,
                }}
              >
                <Form ref={this.formRef} onFinish={this.onFinish}>
                  <Row gutter={16}>
                    <Col className="gutter-row" span={6}>
                      <Form.Item label="关键词：" name="keyword">
                        <Input placeholder="可输入用户名、名称" />
                      </Form.Item>
                    </Col>
                    <Col className="gutter-row" span={4}>
                      <Form.Item label="访问ip：" name="ip">
                        <Input placeholder="请输入" />
                      </Form.Item>
                    </Col>
                    <Col className="gutter-row" span={6}>
                      <Form.Item label="时间：" name="time">
                        <RangePicker
                          style={{ width: '100%' }}
                          // showTime={{ format: 'HH:mm' }}
                          format="YYYY-MM-DD"
                        />
                      </Form.Item>
                    </Col>
                    <Col className="gutter-row" span={4}>
                      <Form.Item label="操作名称" name="description">
                        <Input placeholder="请输入" />
                      </Form.Item>
                    </Col>
                    <Col
                      className="gutter-row"
                      span={4}
                      style={{ textAlign: 'right' }}
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
                  minHeight: window.innerHeight - 280,
                }}
              >
                <Tabs defaultActiveKey="1" onChange={this.callback}>
                  <TabPane tab="登录日志" key="1">
                    <div style={{ backgroundColor: '#fff', padding: 24 }}>
                      <div style={{ backgroundColor: '#fff' }}>
                        <Table
                          style={{ marginTop: 15 }}
                          rowKey="taskId"
                          // rowSelection={rowSelection}
                          columns={columns}
                          dataSource={list}
                          pagination={{
                            showSizeChanger: false,
                            onChange: this.pageChange,
                            pageSize: 10,
                            total,
                            current: pageNum,
                          }}
                          // scroll={{ x: '100%' }}
                        />
                      </div>
                    </div>
                  </TabPane>
                  <TabPane tab="操作日志" key="2">
                    <div
                      style={{
                        backgroundColor: '#fff',
                        padding: 24,
                        paddingTop: 14,
                      }}
                    >
                      <div style={{ backgroundColor: '#fff' }}>
                        <Table
                          style={{ marginTop: 25 }}
                          rowKey="taskId"
                          // rowSelection={rowSelection}
                          columns={columnss}
                          dataSource={lists}
                          pagination={{
                            showSizeChanger: false,
                            onChange: this.pageChanges,
                            pageSize: 10,
                            total: totals,
                            current: pageNums,
                          }}
                        />
                      </div>
                    </div>
                  </TabPane>
                </Tabs>

                <Modal
                  title="操作详情"
                  visible={this.state.details}
                  onCancel={this.handleOk}
                  footer={
                    <Button type="primary" onClick={this.handleOk}>
                      确 定
                    </Button>
                  }
                >
                  <p>日志ID：{record && record.id}</p>
                  <p>
                    操作人：{record && record.name}（{record && record.phone}）
                  </p>
                  <p>访问IP：{record && record.ip}</p>
                  <p>操作时间：{record && record.operateTime}</p>
                  <p>操作名称：{record && record.description}</p>
                  <p>访问地址：{record && record.api}</p>
                  <p>请求参数：{record && record.param}</p>
                  <p>响应参数：{record && record.result}</p>
                </Modal>
              </div>
            </Spin>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default AdministratorLog;
