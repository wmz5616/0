import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Popconfirm,
  Row,
  Spin,
  Switch,
  Table,
} from 'antd';
import React from 'react';
import NewVenues from './components/AddShangquanModal';
const { RangePicker } = DatePicker;

class ShangquanList extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    list: [],
    xxx: false,
    selectedRecord: [],
    selectedRowKeys: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    www: [],
    modalVisible: false,
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
        //列表
        const res = await post(`/guzhe/supermarket/select`, {
          keyword: this.state.keyword,
          startTime: this.state.start_time,
          endTime: this.state.end_time,
          pageNum: this.state.pageNum,
          pageSize: this.state.pageSize,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          this.setState({
            list: res.data.list,
            total: res.data.total,
          });
        } else {
          message.error(res?.msg);
        }
      },
    );
  };

  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
        aa: this.state.selectedRowKeys,
        www: this.state.selectedRecord,
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
        start_time: vas.time
          ? vas.time[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        end_time: vas.time
          ? vas.time[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
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
        searchField2: undefined,
        is_front: undefined,
        searchField1: undefined,
        keyword: undefined,
        start_time: undefined,
        end_time: undefined,
        selectedRowKeys: [],
        selectedRecord: [],
      },
      () => {
        this.getData();
      },
    );
  };

  onChange = (value, dateString) => {
    console.log(dateString);
    this.setState({
      start_time: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
      end_time: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
    });
  };

  aperto = (x) => {
    this.setState({
      xxx: x,
    });
  };

  // 开关场所前台展示
  front = async (checked, id) => {
    const res = await post(`/guzhe/supermarket/status/set`, {
      status: checked ? 1 : 0,
      changeIds: [id],
    });
    if (res.code == 10000) {
      message.success(res.msg);
      this.getData();
    } else {
      message.error(res?.msg);
    }
  };

  handleCancel = () => {
    this.setState({
      isModalOpen: false,
    });
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  //删除
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要删除的数据');
    } else {
      const res = await post(`/guzhe/supermarket/delete`, {
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

  render() {
    const {
      selectedRowKeys,
      total,
      pageNum,
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
        title: '商超名称',
        dataIndex: 'name',
      },
      {
        title: '联系电话',
        dataIndex: 'phone',
      },
      {
        title: '营业时间',
        dataIndex: 'businessHours',
      },
      {
        title: '商超地址',
        dataIndex: 'address',
      },
      {
        title: '启用状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <Switch
                checkedChildren="开启"
                unCheckedChildren="关闭"
                checked={text}
                onChange={(value) => {
                  this.front(value, record.id);
                }}
              />
            </div>
          );
        },
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
      },
      {
        title: '操作',
        dataIndex: '',
        render: (text, record) => {
          return (
            <>
              <span
                className="clickFont"
                onClick={() =>
                  this.setState({
                    modalVisible: true,
                    isAdd: false,
                    id: record.id,
                  })
                }
              >
                编辑
              </span>
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
                <Col
                  className="gutter-row"
                  span={4}
                  style={{ marginRight: 30 }}
                >
                  <Form.Item label="商超名称" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="创建时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
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
            <Row>
              <Col span={6}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  商超信息管理
                </h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button
                    type="primary"
                    onClick={() =>
                      this.setState({ modalVisible: true, isAdd: true })
                    }
                  >
                    +新增商超
                  </Button>
                  <Popconfirm
                    title={
                      <>
                        <div>删除提示</div>
                        <div>
                          <span style={{ color: 'red' }}>
                            删除的内容不可恢复
                          </span>
                          ，<span style={{ color: '#ccc' }}>确定删除吗？</span>
                        </div>
                      </>
                    }
                    onConfirm={() => this.deletes(selectedRowKeys)}
                    // onCancel={cancel}
                    okText="是"
                    cancelText="否"
                  >
                    <Button danger className="mL15">
                      删除
                    </Button>
                  </Popconfirm>
                </div>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              rowSelection={rowSelection}
              rowKey="id"
              columns={columns}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: this.state.pageSize,
                total,
                current: pageNum,
              }}
              // scroll={{ x: '100%' }}
            />

            {this.state.modalVisible && (
              <NewVenues
                handleCancel={() => this.setState({ modalVisible: false })}
                isAdd={this.state.isAdd}
                id={this.state.id}
                getData={this.getData}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default ShangquanList;
