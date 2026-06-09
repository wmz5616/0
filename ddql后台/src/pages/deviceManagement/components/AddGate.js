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
  Alert,
} from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { connect } from 'umi';
import moment from 'moment';
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};
class App extends React.Component {
  formRef = React.createRef();
  state = {
    loading: false,
    gymID: 0,
    aa: true,
    QrCodex: false,
    QrCode:false
  };

  componentDidMount() {
    this.getData();

    var str = [];
    var len = str.length;
    var len = 8;
    var arr = new Array(
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      'A',
      'B',
      'C',
      'D',
      'E',
      'F',
      'G',
      'H',
      'I',
      'J',
      'K',
      'L',
      'M',
      'N',
      'O',
      'P',
      'Q',
      'R',
      'S',
      'T',
      'U',
      'V',
      'W',
      'X',
      'Y',
      'Z',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z',
    );

    for (var i = 0; i < len; i++) {
      var index = Math.floor(Math.random() * 62);
      str += arr[index];
    }
    var ff = str;
    this.setState({
      only: ff,
    });
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        const { dispatch, id, edit } = this.props;
        // //场馆
        // this.props.dispatch({
        //   type: 'myModel/getSetData',
        //   payload: {
        //     limit: 999,
        //   },
        //   url: `/api/admin/gym/lists`,
        //   method: 'GET',
        //   myData: (res) => {
        //     this.setState({
        //       spinning: false,
        //     });
        //     if (res && res.code === 200) {
        //       this.setState({
        //         listss: res.data.lists,
        //       });
        //     } else {
        //       message.error(res.message);
        //     }
        //   },
        // });

        //场所
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
          },
          url: `/api/admin/stadium/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data.lists);
              this.setState({
                stadiumLists: res.data.lists,
              });
            } else {
              message.error(res.message);
            }
          },
        });

        if (edit) {
          this.setState({
            QrCode: false,
            stadiumIDd:edit.stadium_id,
          });
          // console.log(edit);
          this.formRef.current.setFieldsValue({
            device_num: edit.device_num,
            gym_id: edit.gym ? edit.gym.id : 0,
            enable: edit.enable,
            remark: edit.remark,
            stadium_id: edit.stadium_id,
          });
        } else {
          this.setState({
            // QrCode: false,
          });
        }

        console.log(edit);
      },
    );
  };

  handleOk = () => {
    const { handleOk, dispatch, id, getData, add, edit } = this.props;

  
    if (add) {
      this.setState({
        aa: false,
      },()=>{
        handleOk();
      });
     
      // this.formRef.current.validateFields().then((values) => {
      //   dispatch({
      //     type: 'myModel/getSetData',
      //     payload: {
      //       device_type: 3,
      //       device_num: values.device_num, //设备序列号
      //       gym_id: values.gym_id,
      //       enable: values.enable,
      //       remark: values.remark,
      //     },
      //     // dataName: 'developerListData',
      //     method: 'POST',
      //     url: `/api/admin/device/add`,
      //     myData: (res) => {
      //       if (res.code === 200) {
      //         message.success(res.message);
      //         handleOk();
      //         getData();
      //       } else {
      //         message.error(res.message);
      //       }
      //     },
      //   });
      // });
    } else {
      this.formRef.current.validateFields().then((values) => {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            device_type: 3,
            device_num: values.device_num, //设备序列号
            gym_id: values.gym_id,
            enable: values.enable,
            remark: values.remark,
            stadium_id: values.stadium_id,
            id: edit.id,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/device/update`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleOk();
              getData();
            } else {
              message.error(res.message);
            }
          },
        });
      });
    }
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    this.setState({
      aa: false,
    },()=>{
      handleOk();
    });
  };

  handleChange = (value) => {
   //场馆
   this.props.dispatch({
    type: 'myModel/getSetData',
    payload: {
      limit: 999,
      stadium_id:value
    },
    url: `/api/admin/gym/lists`,
    method: 'GET',
    myData: (res) => {
      this.setState({
        spinning: false,
      });
      if (res && res.code === 200) {
        this.setState({
          listss: res.data.lists,
        });
      } else {
        message.error(res.message);
      }
    },
  });







    this.setState(
      {
        stadiumIDd: value,
        QrCode: true,
      },
      () => {
        this.setState(
          {
            QrCode: false,
          },
          () => {
            this.setState(
              {
                QrCode: true,
              },
              () => {
                /* 生成二维码 */
                var qrcode = new QRCode(document.getElementById('qrcode'), {
                  text: `AD_${this.state.stadiumIDd}_${this.state.gymID}_${this.state.only}`, //扫描二维码后的内容
                  width: 128, //二维码的宽
                  height: 128, //二维码的高
                  // colorDark: 'green', //二维码线条颜色
                  // colorLight: '#ffffff', //二维码背景颜色
                  correctLevel: QRCode.CorrectLevel.H, //二维码等级
                });

                this.QrCodes();
              },
            );
          },
        );
      },
    );
  };

  handleChanges = (value) => {
    this.setState(
      {
        gymID: value ? value : 0,
        QrCode: false,
      },
      () => {
        this.setState(
          {
            QrCode: true,
          },
          () => {
            /* 生成二维码 */
            var qrcode = new QRCode(document.getElementById('qrcode'), {
              text: `AD_${this.state.stadiumIDd}_${this.state.gymID}_${this.state.only}`, //扫描二维码后的内容
              width: 128, //二维码的宽
              height: 128, //二维码的高
              // colorDark: 'green', //二维码线条颜色
              // colorLight: '#ffffff', //二维码背景颜色
              correctLevel: QRCode.CorrectLevel.H, //二维码等级
            });
          },
        );
      },
    );
  };

  QrCodes = () => {
    const { handleOk, getData } = this.props;
    const { aa } = this.state;
    console.log(aa)
    if (aa) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          rid: this.state.only,
        },
        url: `/api/admin/device/relate`,
        method: 'GET',
        myData: (res) => {
          if (res && res.code === 200) {
            if (res.data) {
              this.setState({
                QrCode: false,
                QrCodex: true,
                deviceNum:res.data.device_num
              },()=>{
                var timName = document.getElementById('tim');
                var t = 3;
                var timer = setInterval(function(){
                timName.innerHTML = t;
                t--;
                if(t<0){
                clearInterval(timer);
                }
                }, 1000)
  
                setTimeout(() => {
                  handleOk();
                }, 5000);
              
              });
              // message.info(res.message);
              getData();
             
            } else {
              setTimeout(() => {
                this.QrCodes();
              }, 2000);

              // message.error(res.message);
            }
          }
        },
      });
    }
  };

  render() {
    const { add } = this.props;

    const { listss = [], stadiumLists = [], QrCodex } = this.state;

    return (
      <>
        <Modal
          title={add ? '新增设备' : '编辑设备'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={666}
        >
          {add == true && (
            <>
              {QrCodex == false ? (
                <>
                  <Alert
                    style={{ marginBottom: 20 }}
                    message="请先选择设备所需捆绑的场所或场馆，然后用设备扫下方二维码添加。"
                    type="warning"
                    closable
                    showIcon 
                  />
                </>
              ) : (
                <>
                  <Alert
                    message= {<>设备添加成功，SN编码为：{this.state.deviceNum}，<span id="tim">3</span> 秒后会自动关闭本弹窗。</>}
                    type="success"
                    style={{ marginBottom: 20 }}
                    closable
                    showIcon 
                  />
                </>
              )}
            </>
          )}

          <Form ref={this.formRef} {...layout}>
            <Form.Item
              label="所属场所"
              name="stadium_id"
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Select
                allowClear
                showSearch
                placeholder="请选择"
                optionFilterProp="label"
                onChange={this.handleChange}
                disabled={add ? false : true}
              >
                {stadiumLists.map((res) => {
                  return (
                    <Option value={res.id} key={res.name} label={`${res.id}${res.name}`}>
                      {res.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item label="所属场馆">
              <Form.Item name="gym_id" noStyle>
                <Select
                  allowClear
                  showSearch
                  placeholder="请选择"
                  optionFilterProp="label"
                  onChange={this.handleChanges}
                  disabled={add ? false : true}
                >
                  {this.state.stadiumIDd&& <Option value={0}>全部</Option>}
                 
                  {listss.map((res) => {
                    return (
                      <Option value={res.id} key={res.name} label={`${res.id}${res.name}`}>
                        {res.name}
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
              <div style={{ color: '#ccc' }}>不选即全部</div>
            </Form.Item>

            {add !== true ? (
              <>
                <Form.Item
                  label="设备SN编码"
                  name="device_num"
                  rules={[{ required: true, message: '请选择!' }]}
                >
                  <Input disabled />
                </Form.Item>

                <Form.Item
                  label="是否启用"
                  name="enable"
                  rules={[{ required: true }]}
                  initialValue={1}
                >
                  <Radio.Group>
                    <Radio value={1}>是</Radio>
                    <Radio value={0}>否</Radio>
                  </Radio.Group>
                </Form.Item>

                <Form.Item label="备注" name="remark" rules={[{ required: true }]}>
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
              </>
            ) : (
              ''
            )}

            {this.state.QrCode && (
              <div
                style={{
                  border: '1px solid #ccc',
                  padding: 15,
                  height: 170,
                  margin: '0 50px',
                  paddingRight: 0,
                  borderRadius:5
                }}
              >
                <div id="qrcode" style={{ float: 'left', marginTop: 5 }} />
                <img
                  alt="sideImg"
                  src={require('@/assets/images/smj.jpg')}
                  width="350"
                  style={{ float: 'left', marginLeft: 20 }}
                />
              </div>
            )}
          </Form>
        </Modal>
      </>
    );
  }
}

export default connect((allModels) => ({}))(App);
