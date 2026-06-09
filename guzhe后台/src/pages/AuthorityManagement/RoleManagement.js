import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  Form,
  Input,
  message,
  Popconfirm,
  Row,
  Select,
  Spin,
  Switch,
  Table,
} from 'antd';
import React from 'react';
import Empower from './components/Empower';
import NewRole from './components/NewRole';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
//角色管理
class RoleManagement extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    pageSize: 10,
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
        const res = await post(`/guzhe/role/lists`, {
          searchIntStatus: this.state.searchIntStatus,
          keyword: this.state.keyword,
          pageNum: this.state.pageNum,
          pageSize: this.state.pageSize,
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

  showModal = (add, edit) => {
    this.setState({
      NewRoles: true,
      add,
      edit,
    });
  };

  handleOk = () => {
    this.setState({
      NewRoles: false,
    });
  };

  showModalz = (id) => {
    this.setState({
      empower: true,
      idz: id,
    });
  };

  handleOkz = () => {
    this.setState({
      empower: false,
    });
  };

  // 删除函数
  //删除
  deletes = async (ids) => {
    if (ids.length === 0) {
      message.error('请选择需要删除的角色');
    } else {
      const res = await post(`/guzhe/role/delete`, {
        deleteIds: ids,
      });
      if (res && res.code === 10000) {
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  siteFrontReserve = async (v, ids) => {
    if (ids === undefined) {
      message.error('请选择需要操作的角色');
    } else {
      const res = await post(`/guzhe/role/status/set`, {
        enable: v,
        id: ids.join(','),
      });
      if (res && res.code === 200) {
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

  onFinish = (v) => {
    console.log(v);
    this.setState(
      {
        searchIntStatus: v.searchIntStatus,
        keyword: v.keyword,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  resets = () => {
    this.formRef.current.resetFields();
    this.setState(
      {
        searchIntStatus: undefined,
        keyword: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  front = async (checked, id) => {
    //启用状态修改
    const res = await post(`/ddql/role/status/set`, {
      changeId: id,
      status: checked ? 1 : 0,
    });
    if (res && res.code === 10000) {
      message.success(res.msg);
      this.getData();
    } else {
      message.error(res?.msg);
    }
  };

  render() {
    const { list = [], selectedRowKeys, NewRoles, pageNum, total } = this.state;
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
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '说明',
        dataIndex: 'remark',
      },
      {
        title: '启用状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <Switch
              checkedChildren="开启"
              unCheckedChildren="关闭"
              checked={text}
              onChange={(value) => this.front(value, record.id)}
            />
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

              <a className="mL15" onClick={() => this.showModalz(record.id)}>
                授权
              </a>
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
                  <Form.Item label="名称" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="启用状态" name="searchIntStatus">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>启用</Option>
                      <Option value={0}>禁用</Option>
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
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  角色管理
                </h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={() => this.showModal(true)}>
                    +新增角色
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
              loading={this.state.loading}
              rowKey="id"
              rowSelection={rowSelection}
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

            {/* 新建角色模态框 */}
            {NewRoles && (
              <NewRole
                handleOk={this.handleOk}
                add={this.state.add}
                id={this.state.idz}
                getData={this.getData}
                edit={this.state.edit}
              />
            )}

            {this.state.empower && (
              <Empower
                handleOk={this.handleOkz}
                getData={this.getData}
                id={this.state.idz}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default RoleManagement;
