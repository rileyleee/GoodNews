// 버튼 컴포넌트
import React, { ReactNode } from "react";

interface ButtonProps {
  size: ButtonSize;
  color: ButtonColor;
  onClick?: () => void;
  children: ReactNode;
  className?: string;
  isActive?: Boolean;
}

type ButtonSize = "Large" | "Medium" | "Small";
type ButtonColor = "Main" | "Sub" | "Safe" | "Danger" | "Undefined";

const Button = ({ size, onClick, children, color, className, isActive }: ButtonProps) => {
  return <div></div>;
};

export default Button;
