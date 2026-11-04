import { TestBed } from '@angular/core/testing';

import { FileApi } from './file-api';

describe('FileApi', () => {
  let service: FileApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
