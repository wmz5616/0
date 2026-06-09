import React from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  Radio,
  DatePicker,
  Upload,
  Row,
  Col,
  TimePicker,
  InputNumber,
  message,
  Button,
} from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { connect } from 'umi';
import moment from 'moment';
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;
import ImgCrop from 'antd-img-crop';
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 18 },
};
class App extends React.Component {
  formRef = React.createRef();
  state = {
    loading: false,
    fileList: [],
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
        //类型
        const { dispatch, id } = this.props;

        dispatch({
          type: 'myModel/getSetData',
          payload: {},
          url: `/api/admin/member/lists`,
          method: 'GET',
          myData: (res) => {
            if (res && res.code === 200) {
              this.setState({
                adminLists: res.data.lists,
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });

        //运动类型列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {},
          url: `/api/admin/sport_type/lists`,
          method: 'GET',
          myData: (res) => {
            if (res && res.code === 200) {
              this.setState({
                motionTypeLists: res.data.lists,
              });
            } else {
              message.error(res.message);
            }
          },
        });

        if (id) {
          console.log(id);
          //场馆详情
          dispatch({
            type: 'myModel/getSetData',
            payload: {
              id: id,
            },
            url: `/api/admin/gym/info`,
            method: 'GET',
            myData: (res) => {
              if (res && res.code === 200) {
                this.setState({
                  spinning: false,
                });

                const xx = res.data.cover.split(',');
                const cloneCommunities = [];
                xx.map((resd, index) => {
                  cloneCommunities.push({
                    response: {
                      data: {
                        uri: resd,
                        code: 200,
                      },
                    },
                    url: resd,
                    uid: (index + 1).toString(),
                  });
                });

                console.log(res.data.cover);
                this.setState({
                  info: res.data,
                  fileList: cloneCommunities,
                });

                this.formRef.current.setFieldsValue({
                  name: res.data.name, //名称
                  cover: res.data.cover, //封面
                  sport_type: res.data.sport_types.map((res) => res.sport_type.id), //运动类型
                  site_front_reserve: res.data.site_front_reserve, //
                  site_open_start_time: res.data.site_open_start_time
                    ? moment(res.data.site_open_start_time, 'HH:mm')
                    : undefined,
                  site_open_end_time: res.data.site_open_end_time
                    ? moment(res.data.site_open_end_time, 'HH:mm')
                    : undefined,
                  admin_ids: res.data.admin_notice.map((res) => res.admin_id), //通知

                  site_bill_unit: res.data.site_bill_unit, //计费单位
                  site_reserve_before_date: res.data.site_reserve_before_date, //订场提前天数
                  ticket_reserve_before_date: res.data.ticket_reserve_before_date, //订票提前天数
                  site_approach_before_minute: res.data.site_approach_before_minute, //订场可提前进场(分钟)
                  ticket_approach_before_minute: res.data.ticket_approach_before_minute, //订票提前进场分钟
                  order_sites_max:res.data.order_sites_max, //每订单可预订场次数量
                  daily_free_site_order_num:res.data.daily_free_site_order_num,//免费场次单天可预订数量限制
                  daily_free_ticket_order_num:res.data.daily_free_ticket_order_num, //免费票单天预订数量限制
                  is_unique:res.data.is_unique,
                });
              } else {
                message.error(res.message);
                // this.setState({ isSelectForm: true });
              }
            },
          });
        }else{
          this.formRef.current.setFieldsValue({
            order_sites_max:undefined, //每订单可预订场次数量
            daily_free_site_order_num:0,//免费场次单天可预订数量限制
            daily_free_ticket_order_num:0, //免费票单天预订数量限制
          });
        }
      },
    );
  };

  handleOk = () => {
    const { handleOk, dispatch, id, getData, info } = this.props;
    console.log(id);

    this.formRef.current.validateFields().then((values) => {
      var time = values.site_open_start_time.format('HH:mm');
      var hour = time.split(':')[0];
      var min = time.split(':')[1];
      var startTimes = Number(hour * 3600) + Number(min * 60);

      var times = values.site_open_end_time.format('HH:mm');
      var hours = times.split(':')[0];
      var mins = times.split(':')[1];
      var endTimes = Number(hours * 3600) + Number(mins * 60);
      console.log(endTimes);
      console.log(startTimes);

      if (endTimes > startTimes) {
        if (
          values.site_bill_unit % 30 == 0 &&
          ((endTimes - startTimes) / (values.site_bill_unit * 60)) % 1 == 0
        ) {
          dispatch({
            type: 'myModel/getSetData',
            payload: {
              id: id,
              stadium_id: info && info.id, //场所id
              name: values.name, //名称
              cover: this.state.fileList.map((res) => res.response.data.uri).join(','), //封面
              sport_type: values.sport_type.join(','), //运动类型
              site_front_reserve: values.site_front_reserve, //
              site_open_start_time: values.site_open_start_time.format('HH:mm'), //开始时间
              site_open_end_time: values.site_open_end_time.format('HH:mm'), //结束时间
              admin_ids: values.admin_ids && values.admin_ids.join(','), //通知

              site_bill_unit: values.site_bill_unit, //计费单位
              site_reserve_before_date: values.site_reserve_before_date, //订场提前天数
              ticket_reserve_before_date: values.ticket_reserve_before_date, //订票提前天数
              site_approach_before_minute: values.site_approach_before_minute, //订场可提前进场(分钟)
              ticket_approach_before_minute: values.ticket_approach_before_minute, //订票提前进场分钟
              order_sites_max:values.order_sites_max, //每订单可预订场次数量
              daily_free_site_order_num:values.daily_free_site_order_num,//免费场次单天可预订数量限制
              daily_free_ticket_order_num:values.daily_free_ticket_order_num, //免费票单天预订数量限制
              is_unique:values.is_unique,
            },
            // dataName: 'developerListData',Ss
            method: 'POST',
            url: `${id ? '/api/admin/gym/update' : '/api/admin/gym/add'}`,
            myData: (res) => {
              if (res.code === 200) {
                message.success(res.message);
                console.log(9999);
                handleOk();
                getData();
              } else {
                console.log(10000);
                message.error(res.message);
              }
            },
          });
        } else {
          message.error('计费单位(分钟)必须是30的倍数且能整除开放时长');
        }
      } else {
        message.error('结束时间必须大于开始时间');
      }
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  onChangex = (e) => {
    this.setState({
      qwe: e.target.value,
    });
  };

  showModal = () => {
    this.setState({
      binding: true,
    });
  };

  handleOkw = () => {
    this.setState({
      binding: false,
    });
  };

  handleCancelw = () => {
    this.setState({
      binding: false,
    });
  };

  onChange = ({ fileList }) => {
    // setFileList(newFileList);
    // console.log(fileList)

    console.log(fileList);
    this.setState({
      fileList: fileList,
      // cover:newFileList.map(res => res.response.data.uri),
    });
  };
  onPreview = async (file) => {
    let src = file.url;

    if (!src) {
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);

        reader.onload = () => resolve(reader.result);
      });
    }

    const image = new Image();
    image.src = src;
    const imgWindow = window.open(src);
    imgWindow?.document.write(image.outerHTML);
  };

  render() {
    const { info } = this.props;
    console.log(info);

    const { loading, adminLists = [], fileList = [], motionTypeLists = [] } = this.state;

    const props = {
      // aspect:280/186,
      grid: false,
      width: 280,
      height: 186,
      resize: true, //裁剪是否可以调整大小
      resizeAndDrag: true, //裁剪是否可以调整大小、可拖动
      modalTitle: '上传图片', //弹窗标题
      modalWidth: 600, //弹窗宽度
      // quality:0.4
    };

    return (
      <>
        <Modal
          title={`${info ? '新增场馆' : '编辑场馆'}`}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={1000}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item
              label="场馆名称"
              name="name"
              rules={[{ required: true, message: '请输入!' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>场馆图片
                </span>
              }
              name="cover"
            >
              <Form.Item noStyle>
                <ImgCrop {...props}>
                  <Upload
                    action="/ddql/file/upload"
                    listType="picture-card"
                    fileList={fileList}
                    onChange={this.onChange}
                    onPreview={this.onPreview}
                  >
                    {fileList.length < 1 && '+ 上传'}
                  </Upload>
                </ImgCrop>
              </Form.Item>
              <span style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span>
            </Form.Item>

            <Form.Item label="运动类型" name="sport_type" rules={[{ required: true }]}>
              <Select allowClear placeholder="请选择" mode="multiple">
                {motionTypeLists.map((res) => {
                  return (
                    <Option value={res.id} key={res.id}>
                      {res.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item
              label="开放时间"
              name="site_open_start_time"
              rules={[{ required: true, message: '请选择开始时间' }]}
            >
              <TimePicker type="time" format="HH:mm" minuteStep={30} />
            </Form.Item>

            <Form.Item
              name="site_open_end_time"
              rules={[{ required: true, message: '请选择结束时间' }]}
              style={{ position: 'absolute', marginLeft: '350px', marginTop: '-56px' }}
            >
              <TimePicker type="time" format="HH:mm" style={{ width: 150 }} minuteStep={30} />
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>计费单位(分钟)
                </span>
              }
            >
              <Form.Item
                name="site_bill_unit"
                noStyle
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber min={1} />
              </Form.Item>
              <div style={{ color: '#ccc' }}>计费单位必须是30的倍数，且能整除开放时长</div>
            </Form.Item>

            <Form.Item label={<span>订场通知</span>}>
              <Form.Item name="admin_ids" noStyle>
                <Select
                  allowClear
                  mode="multiple"
                  placeholder="请选择通知对象"
                  optionFilterProp="label"
                >
                  {adminLists.map((res) => {
                    return (
                      <Option value={res.id} key={res.id} label={`${res.phone}${res.username}`}>
                        {res.username}({res.phone}){' '}
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
              <div style={{ color: '#ccc' }}>
                <div>当该场馆有新的订场订单时将通过微信公众号发送提醒</div>
                <div>
                  注意：首次被设置为接收通知的账号需
                  <a onClick={() => this.showModal()}>点此进行微信绑定</a>
                </div>
              </div>
            </Form.Item>

            <Form.Item label="场次订单唯一"
             rules={[{ required: true }]}
              name="is_unique" initialValue={1}>
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>

            <Form.Item style={{marginTop: '-30px', marginLeft: '200px'}}>
            <div style={{ color: '#ccc' }}>如果选否，场次排期支持输入场次允许最大预订订单数</div>
            </Form.Item>


            {/* <Form.Item 
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>场次订单唯一
                </span>
              }
            >
              <Form.Item name="is_unique" initialValue={1} noStyle  rules={[{ required: true, message: '请输入!' }]} >
              <Radio.Group style={{marginTop:5}}>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
              </Form.Item>
              <div style={{ color: '#ccc' }}>如果选否，场次排期支持输入场次允许最大预订订单数</div>
            </Form.Item> */}






            <Form.Item
              label="可提前预订天数"
              name="site_reserve_before_date"
              rules={[{ required: true }]}
            >
              <InputNumber min={1} />
            </Form.Item>

            <Form.Item
              label="订场可提前进场(分钟)"
              name="site_approach_before_minute"
              rules={[{ required: true }]}
            >
              <InputNumber min={0} />
            </Form.Item>

            <Form.Item label="每订单可预订场次数量">
              <Form.Item
                name="order_sites_max"
                noStyle
                // rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber  min={1}  />
              </Form.Item>
              <div style={{ color: '#ccc' }}>不支持输入小于1的整数</div>
            </Form.Item>

            <Form.Item label="免费场次单天可预订数量限制">
              <Form.Item
                name="daily_free_site_order_num"
                noStyle
                // rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber min={0} />
              </Form.Item>
              <div style={{ color: '#ccc' }}>为0则不限制</div>
            </Form.Item>

            <Form.Item
              label="可提前订票天数"
              name="ticket_reserve_before_date"
              rules={[{ required: true }]}
            >
              <InputNumber min={0} />
            </Form.Item>

            <Form.Item
              label="订票可提前进场(分钟)"
              name="ticket_approach_before_minute"
              rules={[{ required: true }]}
            >
              <InputNumber min={0} />
            </Form.Item>

            <Form.Item label="免费票单天预订数量限制">
              <Form.Item
                name="daily_free_ticket_order_num"
                noStyle
                // rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber min={0} />
              </Form.Item>
              <div style={{ color: '#ccc' }}>为0则无限制</div>
            </Form.Item>
          </Form>
        </Modal>

        <Modal
          title="微信绑定"
          visible={this.state.binding}
          onOk={() => this.handleOkw()}
          onCancel={() => this.handleCancelw()}
          footer={
            <Button onClick={() => this.handleOkw()} type="primary">
              确定
            </Button>
          }
        >
          <div style={{ textAlign: 'center' }}>
            <img alt="" src={require('@/assets/images/bangding.png')} width="100%" />
            <h1 style={{ fontSize: '20px' }}>微信扫码绑定</h1>
            <span style={{ color: '#ccc' }}>注意：一定要使用微信进行扫码绑定才能完成账号绑定</span>
          </div>
        </Modal>
      </>
    );
  }
}

export default connect((allModels) => ({}))(App);
