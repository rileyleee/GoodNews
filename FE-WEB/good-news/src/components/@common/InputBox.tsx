// input 박스
import styled from "styled-components";

interface InputProps {
  placeholder?: string;
  type?: string;
  value?: string;
  size?: InputSize;
  className?: string;
  onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

type InputSize = "Standard";
type InputColor = "Standard";

type InputStyle = {
  height: string;
  fontSize: string;
};

const InputStyles: Record<InputSize, InputStyle> = {
  Standard: {
    height: "48px",
    fontSize: "20px",
  },
};

const COLOR_STYLES: Record<InputColor, string> = {
  Standard: "#274C77",
};

const StyledInputBox = styled.input<InputProps>`
  height: ${(props) => InputStyles[props.size || "Standard"].height};
  font-size: ${(props) => InputStyles[props.size || "Standard"].fontSize};
  border: 1px solid ${COLOR_STYLES.Standard};
  border-radius: 8px;
  padding: 0 8px;
`;

const InputBox = ({
  placeholder,
  type,
  value,
  onChange,
  size,
  className,
}: InputProps) => {
  return (
    <StyledInputBox
      placeholder={placeholder}
      type={type}
      value={value}
      onChange={onChange}
      className={className}
      size={size}
    />
  );
};

export default InputBox;
