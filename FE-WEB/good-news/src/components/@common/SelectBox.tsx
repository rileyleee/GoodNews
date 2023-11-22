// 드롭다운 박스
import { Fragment, useState } from "react";
import { Listbox, Transition } from "@headlessui/react";
import { LuChevronsUpDown, LuCheck } from "react-icons/lu";

// region 타입
interface Region {
  name: string;
}

interface SelectBoxProps {
  data: Region[];
  className?: string;
  isInActive?: boolean;
}

const SelectBox = ({ data, className, isInActive }: SelectBoxProps) => {
  const [selected, setSelected] = useState(data[0]);
  const buttonClassName = `relative w-full h-12 border-2 border-sky-800 cursor-default rounded-lg bg-white py-3 pl-3 pr-10 text-left focus-visible:ring-2 focus-visible:ring-white/75 focus-visible:ring-offset-2 focus-visible:ring-offset-orange-300 sm:text-sm ${
    isInActive ? "opacity-50 cursor-not-allowed" : ""
  }`;

  return (
    <div className={className}>
      <Listbox value={selected} onChange={isInActive ? setSelected : undefined}>
        <div className="relative">
          <Listbox.Button className={buttonClassName}>
            <span className="block truncate">{selected.name}</span>
            <span className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2">
              <LuChevronsUpDown
                className="h-5 w-5 text-gray-400"
                aria-hidden="true"
              />
            </span>
          </Listbox.Button>
          <Transition
            as={Fragment}
            leave="transition ease-in duration-100"
            leaveFrom="opacity-100"
            leaveTo="opacity-0"
          >
            <Listbox.Options
              className={`absolute mt-1 border-2 border-sky-800 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black/5 focus:outline-none sm:text-sm ${
                isInActive ? "hidden" : ""
              }`}
            >
              {data.map((region, index) => (
                <Listbox.Option
                  key={`person-${index}`}
                  className={({ active }) =>
                    `relative cursor-default select-none py-2 pl-10 pr-4 ${
                      active ? "bg-sky-100 text-sky-900" : "text-gray-900"
                    }`
                  }
                  value={region}
                >
                  {({ selected }) => (
                    <>
                      <span
                        className={`block truncate ${
                          selected ? "font-medium" : "font-normal"
                        }`}
                      >
                        {region.name}
                      </span>
                      {selected ? (
                        <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-sky-600">
                          <LuCheck className="h-5 w-5" aria-hidden="true" />
                        </span>
                      ) : null}
                    </>
                  )}
                </Listbox.Option>
              ))}
            </Listbox.Options>
          </Transition>
        </div>
      </Listbox>
    </div>
  );
};

export default SelectBox;
