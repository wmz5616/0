import { Avatar, List, Tabs, Spin } from 'antd';
import React from 'react';
import classNames from 'classnames';
import styles from './NoticeList.less';
import UserFeedbackDetails from '../../pages/HelpFeedback/components/UserFeedbackDetails';
import OrderDetails from '../../pages/OrderManagement/components/OrderDetails';
import { history, connect, Link } from 'umi';
const { TabPane } = Tabs;
class Login extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    counts: 0,
    count: 0,
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
            limit: 999,
            status: 0,
          },
          url: `/api/admin/feedback/lists`,
          method: 'GET',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data.lists);
              this.setState({
                spinning: false,
              });
              this.setState({
                list: res.data.lists,
                count: res.data.count,
              },()=>{
                window.localStorage.setItem('counts',this.state.count)
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });

        //列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
            status: 1,
          },
          url: `/api/admin/order/refund/lists`,
          method: 'GET',
          myData: (res) => {
            if (res && res.code === 200) {
              this.setState({
                spinning: false,
              });
              console.log(res.data.lists);
              this.setState({
                lists: res.data.lists,
                counts: res.data.count,
              },()=>{
              });
             
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };
  callback = (key) => {
    console.log(key);
  };

  showModal = (add, record) => {
    this.setState({
      newVenues: true,
      add,
      edit: record,
    });
  };

  handleOk = () => {
    this.setState({
      newVenues: false,
    });
  };

  showModalss = (id) => {
    this.setState({
      Id: id,
      detailss: true,
    });
  };

  handleCancels = () => {
    this.setState({
      detailss: false,
    });
  };

  render() {
    const { list = [], lists = [], count, counts } = this.state;

    return (
      <div>
        <Spin spinning={this.state.spinning}>
          <Tabs defaultActiveKey="1" onChange={this.callback}  style={{ paddingTop: 30 }}
          >
         
            <TabPane tab={`退款审核(${counts > 0 ? counts : 0})`} key="1">
              <div className="xxxxs">
                <List
                  itemLayout="horizontal"
                  dataSource={lists}
                  renderItem={(item, index) => {
                    return (
                      index <= 5 && (
                        <List.Item onClick={() => this.showModalss(item.id)}>
                          <List.Item.Meta
                            avatar={<Avatar src={item.apply_user&&(item.apply_user.avatar)}/>}
                            title={
                              <a>
                              {item.stadium.name}-{item.gym && item.gym.name}
                              </a>
                            }
                            description={
                              <>
                                <div
                                  style={{marginBottom:8}}
                                >
                                  {item.reason}
                                </div>

                                {item.order_type == 1 && (
                                    <span
                                      style={{
                                        border: '1px solid #389e55',
                                        padding: '0px 8px',
                                        borderRadius: '10px',
                                        backgroundColor: '#f6ffed',
                                        color: '#389e55',
                                      }}
                                    >
                                      场地
                                    </span>
                                  )}
                                  {item.order_type == 2 && (
                                    <span
                                      style={{
                                        border: '1px solid #d46b08',
                                        padding: '0px 8px',
                                        borderRadius: '10px',
                                        backgroundColor: '#fff7e6',
                                        color: '#d46b08',
                                      }}
                                    >
                                      门票
                                    </span>
                                  )}
                                <span style={{ color: '#ccc',marginLeft:5 }}>{item.created_at}</span>

                                <span style={{float:'right'}}>{item.amount}</span>
                          
                              </>
                            }
                          />
                        </List.Item>
                      )
                    );
                  }}
                />
              </div>
              <div className={styles.bottomBar}>
                <Link to={`/OrderManagement/RefundReview`}>查看更多</Link>
              </div>
            </TabPane>

            <TabPane tab={<div style={{marginRight:15}}>{`用户反馈(${count > 0 ? (count) : 0})`}</div>} key="2">
              <div className="xxxxs">
                <List
                  itemLayout="horizontal"
                  dataSource={list}
                  renderItem={(item, index) => {
                    {
                      console.log(index);
                    }
                    return (
                      index <= 5 && (
                        <List.Item  onClick={() => this.showModal(false, item)}>
                          <List.Item.Meta
                            avatar={<Avatar src={item.user&&(item.user.avatar)}/>}
                            title={<a>{item.user&&(item.user.username)} </a>}
                            description={
                              <>
                                <div>
                                  {item.content}
                                </div>
                                <span style={{ color: '#ccc' }}>{item.created_at}</span>
                              </>
                            }
                          />
                        </List.Item>
                      )
                    );
                  }}
                />
              </div>
              <div className={styles.bottomBar}>
                <Link to={`/HelpFeedback/UserFeedback`}>查看更多</Link>
              </div>
            </TabPane>
          </Tabs>

          {this.state.newVenues && (
            <UserFeedbackDetails
              handleOk={this.handleOk}
              getData={this.getData}
              edit={this.state.edit}
            />
          )}

          {this.state.detailss && (
            <OrderDetails
              handleCancels={this.handleCancels}
              Id={this.state.Id}
              getData={this.getData}
            />
          )}
        </Spin>
      </div>
    );
  }
}

export default connect()(Login);
