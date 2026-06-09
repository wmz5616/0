import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Row,
  Select,
  Spin,
  Table,
} from 'antd';
import React from 'react';
import { history, Link } from 'umi';
import EditStatus from './components/EditStatus';
const { Option } = Select;
const { RangePicker } = DatePicker;
//用户管理

class Login extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    pageSize: 10,
  };

  componentDidMount() {
    const state = this.props.location?.state || {};
    if (state?.userid) {
      this.formRef.current.setFieldsValue({
        id: state.userid,
      });
      this.setState(
        {
          id: state.userid,
        },
        () => {
          this.getData();
        },
      );
    } else {
      this.getData();
    }
  }

  deletes = async (e) => {
    const res = await post('/api/admin/user/delete', {
      id: e,
    });
    if (res && res.code == 10000) {
      message.success(res.msg);
      this.getData();
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
        //列表
        const res = await post('/guzhe/user/lists', {
          keyword: this.state.keyword,
          searchStrField1: this.state.searchStrField1,
          searchField1: this.state.searchField1,
          searchField2: this.state.searchField2,
          searchField3: this.state.searchField3,
          pageNum: 1,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          has_certification: this.state.has_certification,
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
            selectedRowKeys: [],
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

  showModal = (add, id, record) => {
    console.log(id);

    if (id.length > 0) {
      this.setState({
        newVenues: true,
        ids: id.join(','),
        record,
      });
    } else {
      message.error('请选择要编辑状态的用户');
    }
  };

  handleOk = () => {
    this.setState({
      newVenues: false,
    });
  };

  onFinish = (vas) => {
    this.setState(
      {
        keyword: vas.keyword,
        searchStrField1: vas.searchStrField1,
        searchField1: vas.searchField1,
        searchField2: vas.searchField2,
        searchField3: vas.searchField3,
        pageNum: 1,
        startTime: vas.time
          ? vas.time[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTime: vas.time
          ? vas.time[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
        has_certification: vas.has_certification,
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
        searchStrField1: undefined,
        searchField1: undefined,
        searchField2: undefined,
        searchField3: undefined,
        startTime: undefined,
        endTime: undefined,
        has_certification: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  export = () => {
    const that = this;
    let xhr = new XMLHttpRequest();
    let fileName = `用户管理.xls`; // 文件名称
    xhr.open('POST', `/guzhe/user/export`, true);
    xhr.responseType = 'blob';
    xhr.setRequestHeader('Content-Type', 'application/json');
    const data = { searchIds: this.state.selectedRowKeys };

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
            var a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download == 'undefined') {
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
  render() {
    const { selectedRowKeys, total, pageNum } = this.state;
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
        title: '头像',
        dataIndex: 'avatar',
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
        title: '昵称',
        dataIndex: 'nickname',
      },
      {
        title: '手机号码',
        dataIndex: 'phone',
      },

      {
        title: '状态',
        dataIndex: 'lock',
        render: (text, record) => {
          return (
            <div>
              <>
                {record.lock == 0 && <span className="luSe">正常</span>}
                {record.lock == 1 && <span className="red">短期锁定</span>}
                {record.lock == 2 && <span className="red">永久锁定</span>}
              </>
            </div>
          );
        },
      },
      {
        title: '注册时间',
        dataIndex: 'createTime',
      },
      {
        title: '实名认证',
        dataIndex: 'name',
        render: (res, record) => (
          <div>{record.has_certification == 1 ? res : '未实名'}</div>
        ),
      },

      {
        title: '锁定原因',
        dataIndex: 'lockReason',
      },
      {
        title: '自动解锁时间',
        dataIndex: 'lock_expired_at',
        render: (text, record) => {
          return <div>{record.lockExpiredAt ? record.lockExpiredAt : ''}</div>;
        },
      },
      {
        title: '备注',
        dataIndex: 'remark',
      },

      {
        title: '操作',
        width: 170,
        dataIndex: 'front_reserve',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(true, [record.id], record)}>
                编辑状态
              </a>
              {/* <a
                className="mL15"
                style={{ color: '#1890ff', cursor: 'pointer' }}
                onClick={() => {
                  history.push(`/userManagement/UserDetails?id=${record.id}`);
                }}
              >
                详情
              </a> */}
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
                <Col className="gutter-row" span={5}>
                  <Form.Item label="昵称" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={5}>
                  <Form.Item label="手机号码" name="searchStrField1">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={5}>
                  <Form.Item label="状态" name="searchField1">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>正常</Option>
                      <Option value={1}>短期锁定</Option>
                      <Option value={2}>永久锁定</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={6}>
                  <Form.Item label="注册时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="实名认证" name="searchField2">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>已实名</Option>
                      <Option value={0}>未实名</Option>
                    </Select>
                  </Form.Item>
                </Col>
                {/* <Col className="gutter-row" span={5}>
                  <Form.Item label="用户身份" name="searchField3">
                    <Select placeholder="请选择">
                      <Option value={0}>普通用户</Option>
                      <Option value={1}>会员</Option>
                    </Select>
                  </Form.Item>
                </Col> */}
                <Form.Item style={{ position: 'absolute', right: 20 }}>
                  <Button type="primary" htmlType="submit">
                    查询
                  </Button>

                  <Button className="mL15" onClick={this.resets}>
                    重置
                  </Button>
                </Form.Item>
              </Row>
            </Form>
          </div>

          <div
            style={{
              backgroundColor: '#fff',
              padding: 20,
              minHeight: window.innerHeight - 310,
            }}
          >
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  用户管理
                </h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={this.export}>
                    导出
                  </Button>
                  {/* <Button type="primary" onClick={() => this.showModal(true, selectedRowKeys)}>
                    编辑状态
                  </Button> */}
                </div>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 10 }}
              rowSelection={null}
              rowKey="id"
              columns={columns}
              scroll={{ y: 1000 }}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: pageNum,
              }}
              // scroll={{ x: '100%' }}
            />

            {this.state.newVenues && (
              <EditStatus
                handleOk={this.handleOk}
                getData={this.getData}
                ids={this.state.ids}
                records={this.state.record}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default Login;
