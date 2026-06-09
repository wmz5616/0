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
  Dropdown,
  Menu,
  Switch,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect } from 'umi';
import AddAdministrator from './components/AddAdministrator';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import ExportJsonExcel from 'js-export-excel';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
//管理员管理
class Login extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    xxx: false,
    placeLists: [],
    selectedRecord: [],
    aa: [],
    www: [],
    shopList: [],
    roleList: [],
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
        //管理员列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchField1: this.state.searchField1,
            searchIntStatus: this.state.searchIntStatus,
            searchStrField1: this.state.searchStrField1,
            keyword: this.state.keyword,
            searchField2: this.state.searchField2,
            pageNum: this.state.pageNum,
            pageSize: 10,
          },
          url: `/ddql/admin/lists`,
          method: 'POST',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 10000) {
              this.setState({
                list: res.data.list,
                total: res.data.total,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            pageSize: 999,
          },
          url: `/ddql/role/lists`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              this.setState({
                roleList: res.data.list,
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

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
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

  // 删除函数
  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要删除的管理员');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          deleteIds: ids,
        },
        url: `/ddql/admin/delete`,
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
    }
  };

  onFinish = (vars) => {
    this.setState(
      {
        searchField1: vars.searchField1,
        searchIntStatus: vars.searchIntStatus,
        searchStrField1: vars.searchStrField1,
        keyword: vars.keyword,
        searchField2: vars.searchField2,
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
        searchField1: undefined,
        searchIntStatus: undefined,
        searchStrField1: undefined,
        keyword: undefined,
        searchField2: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  aperto = (x) => {
    this.setState({
      xxx: x,
    });
  };

  setStatus = (value, values) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        roleIds: values.roles ? values.roles.map((xx) => xx.id) : [],
        status: value ? 1 : 0,
        password: values.password,
        name: values.name,
        account: values.account,
        id: values.id,
      },
      url: `/ddql/admin/update`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  render() {
    const {
      list = [],
      selectedRowKeys,
      NewRoles,
      listx = [],
      lists = [],
      total,
      pageNum,
      xxx,
      placeLists = [],
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onSelectAll: (selected, selectedRows, changeRows) => {
        console.log(selected, selectedRows, changeRows);
        this.setState({
          selectedRowKeys: selected
            ? selectedRows
                .filter((res) => res)
                .map((res) => res.id)
                .concat(this.state.aa)
            : [],
          selectedRecord: selected
            ? selectedRows
                .filter((res) => res)
                .map((res) => res)
                .concat(this.state.www)
            : [],
        });
      },
      // onChange: this.onSelectChange,
      onSelect: (record, selected, selectedRows, nativeEvent) => {
        if (!selected) {
          this.setState(
            {
              selectedRowKeys: this.state.selectedRowKeys.filter((res) => res != record.id),
              selectedRecord: this.state.selectedRecord.filter((res) => res.id != record.id),
            },
            () => {
              console.log(this.state.selectedRecord);
            },
          );
        } else {
          console.log(record, selected, selectedRows, nativeEvent);
          this.setState(
            {
              selectedRowKeys: [
                ...this.state.aa,
                ...selectedRows.filter((res) => res).map((res) => res.id),
              ],
              selectedRecord: [...this.state.www, ...selectedRows.filter((res) => res)],
            },
            () => {
              console.log(this.state.selectedRecord);
            },
          );
        }
        console.log(record, selected, selectedRows, nativeEvent);
      },
    };

    const menu = (
      <Menu>
        <Menu.Item key="1" onClick={this.downloadExcel}>
          <a>全部</a>
        </Menu.Item>
        <Menu.Item key="2" onClick={this.Select}>
          <a>选中</a>
        </Menu.Item>
      </Menu>
    );

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '姓名',
        dataIndex: 'name',
      },
      {
        title: '手机',
        dataIndex: 'account',
      },

      {
        title: <span> 角色</span>,
        dataIndex: 'roles',
        render: (text, record) => {
          return <div>{record.roles && record.roles.map((as) => as.name).join('、')}</div>;
        },
      },
      {
        title: '是否启用',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <Switch
              checkedChildren="开启"
              unCheckedChildren="关闭"
              checked={text}
              onChange={(value) => this.setStatus(value, record)}
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

              {record.id != 1 && (
                <>
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
              )}
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
                <Col className="gutter-row" span={5}>
                  <Form.Item label="关键词" name="keyword">
                    <Input placeholder="请输入姓名或手机号搜索" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={3}>
                  <Form.Item label="启用状态" name="searchIntStatus">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>启用</Option>
                      <Option value={0}>禁用</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="角色" name="searchField1">
                    <Select placeholder="请选择">
                      {this.state.roleList.map((res) => {
                        return (
                          <Option value={res.id} key={res.id} label={`${res.id}${res.name}`}>
                            {res.name}
                          </Option>
                        );
                      })}
                    </Select>
                  </Form.Item>
                </Col>
                {/* <Col className="gutter-row" span={4}>
                  <Form.Item label="场所" name="stadium_id">
                    <Select allowClear showSearch placeholder="请选择" optionFilterProp="label">
                      {placeLists.map((res) => {
                        return (
                          <Option value={res.id} key={res.id} label={`${res.id}${res.name}`}>
                            {res.name}
                          </Option>
                        );
                      })}
                    </Select>
                  </Form.Item>
                </Col> */}

                <Col className="gutter-row" style={{ textAlign: 'right', flex: '1 0 220px' }}>
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
            style={{ backgroundColor: '#fff', padding: 20, minHeight: window.innerHeight - 280 }}
          >
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>管理员管理</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={() => this.showModal(true)}>
                    +新增管理员
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
                total,
                current: pageNum,
              }}
              // scroll={{ x: '100%' }}
            />

            {/* 新建角色模态框 */}
            {NewRoles && (
              <AddAdministrator
                handleOk={this.handleOk}
                add={this.state.add}
                getData={this.getData}
                edit={this.state.edit}
                shopList={this.state.shopList}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(Login);
