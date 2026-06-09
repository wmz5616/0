import React from 'react';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Table,
  Select,
  Radio,
  DatePicker,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect } from 'umi';
import AddTicket from './components/AddTicket';
import PrintTemplate from './components/PrintTemplate';

// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;

class NoticeNotice extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
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
        //设备列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            device_type: 2,
            id: this.state.id,
            device_name: this.state.device_name,
            device_num: this.state.device_num,
            admin_id: this.state.admin_id,
            enable: this.state.enable,
            remark: this.state.remark,
            page: this.state.pageNum,
            limit: 10,
          },
          url: `/api/admin/device/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data.lists);
              this.setState({
                list: res.data.lists,
                total: res.data.count,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });

        //场馆
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
          },
          url: `/api/admin/member/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data.lists);
              this.setState({
                listss: res.data.lists,
              });
            } else {
              message.error(res.message);
            }
          },
        });
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

  showModalz = (adds) => {
    this.setState({
      Print: true,
      adds,
    });
  };

  handleOk = () => {
    this.setState({
      NewRoles: false,
      Print: false,
    });
  };

  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要删除的公告');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/device/delete`,
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

  siteFrontReserve = (v, ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的设备');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          enable: v,
          id: ids.join(','),
        },
        url: `/api/admin/device/enable`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            this.setState({
              selectedRowKeys: [],
            });
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

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        device_num: undefined,
        admin_id: undefined,
        enable: undefined,
        remark: undefined,
        id: undefined,
        device_name: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  onFinish = (res) => {
    console.log(res);
    this.setState(
      {
        device_name: res.device_name,
        id: res.id,
        device_num: res.device_num,
        admin_id: res.admin_id,
        enable: res.enable,
        remark: res.remark,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  render() {
    const { list = [], selectedRowKeys, NewRoles, edit, listss = [] } = this.state;
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
        title: '设备名称',
        dataIndex: 'device_name',
      },
      {
        title: '终端号',
        dataIndex: 'device_num',
      },
      {
        title: '是否启用',
        dataIndex: 'enable',
        render: (text, record) => {
          return (
            <div>
              <span>
                {record.enable === 1 ? (
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
        title: '备注',
        dataIndex: 'remark',
      },
      {
        title: '创建时间',
        dataIndex: 'created_at',
      },

      {
        title: '操作',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>编辑</a>

              {record.enable == 1 ? (
                <a className="mL15" onClick={() => this.siteFrontReserve(0, [record.id])}>
                  禁用
                </a>
              ) : (
                <a className="mL15" onClick={() => this.siteFrontReserve(1, [record.id])}>
                  启用
                </a>
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
      <PageContainer
        header={{
          title: ``,
        }}
      >
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="ID" name="id">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="设备名称" name="device_name">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="管理员" name="admin_id">
                    <Select allowClear showSearch placeholder="请选择" optionFilterProp="label">
                      {listss.map((res) => {
                        return (
                          <Option value={res.id} key={res.id} label={`${res.username}${res.phone}`}>
                            {res.username}（ {res.phone}）
                          </Option>
                        );
                      })}
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="终端号" name="device_num">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="是否启用" name="enable">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>是</Option>
                      <Option value={0}>否</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={3} style={{ textAlign: 'right' }}>
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>

                    <Button className="mL15" onClick={this.resets}>
                      重置
                    </Button>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="备注" name="remark">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row>
              <Col span={6}>
             
                <Row>
                <Col>   <h1 style={{ fontWeight: '600', fontSize: '18px' }}>小票机管理</h1></Col>
                <Col> <Button onClick={() => this.showModalz(true)} className='bxlanSes' style={{marginLeft:15}}>打印模板设置</Button></Col>
                </Row>
               
              </Col>

              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={() => this.showModal(true)}>
                    +新增设备
                  </Button>

                  <Button
                    className="mL15 bxluSe"
                    onClick={() => this.siteFrontReserve(1, selectedRowKeys)}
                  >
                    启用
                  </Button>
                  <Button
                    className="mL15 bxHuang"
                    onClick={() => this.siteFrontReserve(0, selectedRowKeys)}
                  >
                    禁用
                  </Button>

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
              <AddTicket
                handleOk={this.handleOk}
                add={this.state.add}
                getData={this.getData}
                edit={edit}
              />
            )}
            {this.state.Print && (
              <PrintTemplate
                handleOk={this.handleOk}
                adds={this.state.adds}
                getData={this.getData}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(NoticeNotice);
