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
  Select,
  Spin,
  Table,
} from 'antd';
import React from 'react';
import NewNotice from './components/NewNotice';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
//通知公告
//通知公告
//通知公告
class NoticeNotice extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    xxx: false,
    selectedRowKeys: [],
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
        const res = await post(`/guzhe/notice/getNoticeList`, {
          searchStatus: this.state.searchStatus,
          searchType: this.state.searchType,
          keyword: this.state.keyword,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          pageNum: this.state.pageNum,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code ==10000) {
          console.log(res.data);
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

  showModal = (add, record) => {
    this.setState({
      NewRoles: true,
      add,
      edit: record,
    });
  };

  handleOk = () => {
    this.setState({
      NewRoles: false,
    });
  };

  //删除
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要删除的公告');
    } else {
      const res = await post(`/guzhe/notice/delete`, {
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

  onFinish = (res) => {
    console.log(res);
    this.setState(
      {
        searchStatus: res.searchStatus,
        keyword: res.keyword,
        searchType: res.searchType,
        startTime:
          res.publishTime && res.publishTime[0].format('YYYY-MM-DD 00:00:00'),
        endTime:
          res.publishTime && res.publishTime[1].format('YYYY-MM-DD 23:59:59'),
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  siteFrontReserve = async (v, ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的公告');
    } else {
      const res = await post(`/guzhe/notice/public/set`, {
        searchIntStatus: v,
        searchIds: ids,
      });
      if (res && res.code == 10000) {
        this.setState({
          selectedRowKeys: [],
        });
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  siteFrontReserves = async (v, ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的公告');
    } else {
      const res = await post(`/guzhe/notice/top/set`, {
        searchIntStatus: v,
        searchIds: ids,
      });
      if (res && res.code == 10000) {
        this.setState({
          selectedRowKeys: [],
        });
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        searchStatus: undefined,
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        searchType: undefined,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  render() {
    const { list = [], selectedRowKeys, NewRoles, edit } = this.state;
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
        title: '标题',
        dataIndex: 'title',
      },

      {
        title: '发布时间',
        dataIndex: 'publishTime',
      },

      {
        title: '创建时间',
        dataIndex: 'createTime',
      },
      {
        title: '是否置顶',
        dataIndex: 'isTop',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text == 1 ? (
                  <span className="luSe">是</span>
                ) : (
                  <span className="red">否</span>
                )}
              </span>
            </div>
          );
        },
      },
      {
        title: '发布状态',
        dataIndex: 'isPublish',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text == 1 ? (
                  <span className="luSe">已发布</span>
                ) : (
                  <span className="huangse">未发布</span>
                )}
              </span>
            </div>
          );
        },
      },
      {
        title: '操作',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>编辑</a>

              {record.isTop == 1 ? (
                <a
                  className="mL15"
                  onClick={() => this.siteFrontReserves(0, [record.id])}
                >
                  取消置顶
                </a>
              ) : (
                <a
                  className="mL15"
                  onClick={() => this.siteFrontReserves(1, [record.id])}
                >
                  置顶
                </a>
              )}

              {/* {record.isPublish == 1 ? (
                <a className="mL15" onClick={() => this.siteFrontReserve(0, [record.id])}>
                  取消发布
                </a>
              ) : (
                <a className="mL15" onClick={() => this.siteFrontReserve(1, [record.id])}>
                  发布
                </a>
              )} */}

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
                <Col className="gutter-row" span={4}>
                  <Form.Item label="关键字" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="类型" name="searchType">
                    <Select  placeholder="请选择">
                      <Option value={1}>通知公告</Option>
                      <Option value={2}>活动推广</Option>
                    </Select>
                  </Form.Item>
                </Col>

                {/* <Col className="gutter-row" span={4}>
                  <Form.Item label="置顶" name="top">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>是</Option>
                      <Option value={0}>否</Option>
                    </Select>
                  </Form.Item>
                </Col> */}

                <Col className="gutter-row" span={4}>
                  <Form.Item label="发布状态" name="searchStatus">
                    <Select placeholder="请选择">
                      <Option value={true}>已发布</Option>
                      <Option value={false}>未发布</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="发布时间" name="publishTime">
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
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  通知公告
                </h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={() => this.showModal(true)}>
                    +新增通知
                  </Button>

                  {/* <Button
                    className="mL15 bxluSe"
                    onClick={() => this.siteFrontReserve(1, this.state.selectedRowKeys)}
                  >
                    发布
                  </Button> */}
                  {/* <Button
                    className="mL15 bxHuang"
                    onClick={() => this.siteFrontReserve(0, this.state.selectedRowKeys)}
                  >
                    取消发布
                  </Button>

                  <Button
                    className="mL15 bxluSe"
                    onClick={() => this.siteFrontReserves(1, this.state.selectedRowKeys)}
                  >
                    置顶
                  </Button>
                  <Button
                    className="mL15 bxHuang"
                    onClick={() => this.siteFrontReserves(0, this.state.selectedRowKeys)}
                  >
                    取消置顶
                  </Button> */}

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
                    onConfirm={() => this.deletes(this.state.selectedRowKeys)}
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
              loading={this.state.loading}
              rowKey="id"
              rowSelection={rowSelection}
              columns={columns}
              dataSource={list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total: this.state.total,
                current: this.state.pageNum,
              }}
              // scroll={{ x: '100%' }}
            />

            {/* 新建角色模态框 */}
            {NewRoles && (
              <NewNotice
                handleOk={this.handleOk}
                add={this.state.add}
                getData={this.getData}
                edit={edit}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default NoticeNotice;
