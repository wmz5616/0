// 全局共享数据示例
import { useState } from 'react';

const GlobalModel = () => {
  const [name, setName] = useState<string>('张三');
  // 从 localStorage 读取配置，优先使用接口保存的数据
  const savedConfig = JSON.parse(localStorage.getItem('config') || 'null');
  const [fInfo, setFInfo] = useState<{
    version: string;
    miitbeian: string;
    logo: string;
    name: string;
  }>({
    version: savedConfig?.version || '1.0.0',
    miitbeian: savedConfig?.miitbeian || '粤ICP备05004358号-18',
    logo: savedConfig?.logo || '/logo.png',
    name: savedConfig?.name || '骨哲屏幕店管理系统',
  });
  const [currentUser, setCurrentUser] = useState<any>(
    JSON.parse(localStorage.getItem('config') || 'null') || {},
  ); // 当前用户信息
  return {
    name,
    setName,
    fInfo,
    setFInfo,
    currentUser,
    setCurrentUser,
  };
};

export default GlobalModel;
