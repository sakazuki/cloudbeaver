/*
 * CloudBeaver - Cloud Database Manager
 * Copyright (C) 2020-2021 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0.
 * you may not use this file except in compliance with the License.
 */

import { AnnotationsMap, observable } from 'mobx';
import { useState } from 'react';

export function useObjectRef<T>(
  init: T,
  update?: Partial<T>,
  observed?: boolean | AnnotationsMap<T, never>,
  bind?: Array<keyof T>
): T {
  const [ref] = useState(() => {
    if (observed) {
      return observable.object(init, typeof observed === 'object' ? observed : undefined, { deep: false });
    }

    if (bind) {
      bindFunctions(init, bind);
    }

    return init;
  });

  if (update) {
    Object.assign(ref, update);

    if (bind) {
      bind = bind.filter(key => key in update);
    }
  } else {
    Object.assign(ref, init);
  }

  if (bind && bind.length > 0) {
    bindFunctions(init, bind);
  }

  return ref;
}

function bindFunctions<T>(object: T, keys: Array<keyof T>): void {
  for (const key of keys) {
    const value = object[key];

    if (typeof value === 'function') {
      object[key] = value.bind(object);
    }
  }
}
