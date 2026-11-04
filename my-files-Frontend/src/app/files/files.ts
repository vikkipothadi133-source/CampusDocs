import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { finalize, timeout } from 'rxjs';
import { RouterOutlet } from '@angular/router';
import { Header } from '../header/header';


type MessageType = 'info' | 'success' | 'error' | '';
type FilterType = 'All' | 'Documents' | 'Images' | 'PDFs';

@Component({
    selector: 'app-files',
    standalone: true,
    imports: [FormsModule, DatePipe, RouterOutlet, Header],
    templateUrl: './files.html',
    styleUrl: './files.css'
})
export class Files implements OnInit {
    private http = inject(HttpClient);

    // Existing Spring Boot API
    apiUrl = 'http://localhost:8081/files/v1';

    // State (signals so the UI always refreshes)
    files = signal<any[]>([]);


    // selectedFile = signal<File | null>(null);
    selectedFiles = signal<File[]>([]);
    checkedIds = signal<number[]>([]);
    uploadIssues = signal<any[]>([]);   
    allChecked = computed(() =>
    this.filteredFiles().length > 0 &&
    this.filteredFiles().every((f) => this.checkedIds().includes(f.id)));     


    searchText = signal('');
    selectedType = signal<FilterType>('All');

    message = signal('');
    messageType = signal<MessageType>('');
    isUploading = signal(false);
    isLoading = signal(false);

    sections = ['Attendance', 'Students', 'Staf', 'TimeTable', 'Syllabus'];
    activeSection = signal('Attendance');

    // Filtered list, recalculated automatically
  filteredFiles = computed(() => {
    const search = this.searchText().toLowerCase().trim();
    return this.files().filter((f) =>
        String(f.fileName || '').toLowerCase().includes(search)
    );
});

    // Load saved files when the page opens / reloads
    ngOnInit(): void {
        this.loadUserDetails();
        this.loadFiles();
    }

    // Get all saved files
    loadFiles(): void {
        this.isLoading.set(true);
        //changed here 
        this.http.get<any>(this.apiUrl + '/all', {
            params: new HttpParams().set('section', this.activeSection())
        })
            .pipe(
                timeout({ first: 15000 }),
                finalize(() => this.isLoading.set(false))
            )
            .subscribe({
                next: (response) => {
                    if (Array.isArray(response)) {
                        this.files.set(response);
                    } else if (Array.isArray(response?.data)) {
                        this.files.set(response.data);
                    } else if (Array.isArray(response?.content)) {
                        this.files.set(response.content);
                    } else {
                        this.files.set([]);
                        console.error('Unexpected file-list response:', response);
                    }
                },
                error: (error) => {
                    console.error('Loading files failed:', error);
                    this.showMessage('Could not load files. Check the backend API / CORS.', 'error');
                }
            });
    }

   onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
        this.selectedFiles.set(Array.from(input.files));
        this.uploadIssues.set([]);
        this.showMessage(input.files.length + ' file(s) selected. Click Upload.', 'info');
    }
}
removeSelected(index: number): void {
    this.selectedFiles.update((list) => list.filter((_, i) => i !== index));
}

  updateFile(): void {
    const list = this.selectedFiles();
    if (list.length === 0) {
        this.showMessage('Please select files first.', 'error');
        return;
    }
    if (this.isUploading()) return;

    const formData = new FormData();
    list.forEach((f) => formData.append('files', f));
    formData.append('section', this.activeSection());

    this.isUploading.set(true);
    this.showMessage('Uploading ' + list.length + ' file(s)...', 'info');

    this.http.post<any[]>(this.apiUrl + '/upload', formData)
        .pipe(timeout({ first: 300000 }), finalize(() => this.isUploading.set(false)))
        .subscribe({
            next: (results) => {
                const ok = results.filter((r) => r.status === 'UPLOADED').length;
                this.uploadIssues.set(results.filter((r) => r.status !== 'UPLOADED'));
                this.showMessage(ok + ' of ' + results.length + ' uploaded.',
                    ok === results.length ? 'success' : 'error');
                this.selectedFiles.set([]);
                this.clearFileInput();
                this.loadFiles();
            },
            error: () => this.showMessage('Upload failed. Check the backend.', 'error')
        });
}

    showMessage(text: string, type: MessageType): void {
        this.message.set(text);
        this.messageType.set(type);
    }
    //it converts the file size into a readable format
    formatSize(bytes: number): string {
        if (!bytes) {
            return '0 KB';
        }
        if (bytes < 1024 * 1024) {
            return (bytes / 1024).toFixed(1) + ' KB';
        }
        return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
    }

    //picks files /input form file manager
    openFilePicker(): void {
        document.getElementById('fileInput')?.click();
    }

    //cacel selection stpos upload and clears the input
   cancelSelection(): void {
    this.selectedFiles.set([]);
    this.message.set('');
    this.messageType.set('');
    this.clearFileInput();
}
    //it returns the symbol according to files 
    getSymbol(file: any): string {
        const type = String(file.contentType || file.type || '');
        if (type === 'application/pdf') return 'PDF';
        if (type.startsWith('image/')) return 'IMG';
        return 'FILE';
    }

    // to clear the elier picked input
    private clearFileInput(): void {
        const input = document.getElementById('fileInput') as HTMLInputElement | null;
        if (input) {
            input.value = '';
        }
    }

    fileUrl(file: any): string {
        return `${this.apiUrl}/view/${file.id}`;
    }


    selectSection(name: string): void {
        this.activeSection.set(name);
        this.searchText.set('');
        this.selectedType.set('All');
        this.cancelSelection();
        this.files.set([]);
        this.loadFiles();
        this.selectedRecord.set(null);
    }

    selectedRecord = signal<any | null>(null);

    selectRecord(file: any): void {
        this.selectedRecord.set(file);
    }

    typeLabel(file: any): string {
        const t = String(file?.contentType || '');
        if (t === 'application/pdf') return 'PDF Document';
        if (t.startsWith('image/')) return t.replace('image/', '').toUpperCase() + ' Image';
        if (t.includes('word')) return 'Word Document';
        if (t.includes('sheet') || t.includes('excel')) return 'Excel Spreadsheet';
        if (t.startsWith('text/')) return 'Text Document';
        return t || 'Unknown';
    }
    // Profile dropdown visibility
    // Profile dropdown visibility
    profileOpen = signal(false);

    // Logged-in user details
    userDetails = signal<any>(null);

    // Load user details from localStorage
    loadUserDetails(): void {
        const storedUser = localStorage.getItem('user');

        console.log('Stored user:', storedUser);

        if (storedUser) {
            try {
                const user = JSON.parse(storedUser);
                this.userDetails.set(user);

                console.log('Loaded user details:', user);
            } catch (error) {
                console.error('Invalid user data:', error);
            }
        }
    }

    // Open and close profile
    toggleProfile(): void {
        this.profileOpen.update(value => !value);
    }




    downloadUrl(file: any): string {
    return `${this.apiUrl}/download/${file.id}`;
}

toggleChecked(id: number): void {
    this.checkedIds.update((ids) =>
        ids.includes(id) ? ids.filter((x) => x !== id) : [...ids, id]);
}

toggleAll(): void {
    this.checkedIds.set(this.allChecked() ? [] : this.filteredFiles().map((f) => f.id));
}

deleteOne(file: any): void {
    if (!confirm('Delete "' + file.fileName + '"? This cannot be undone.')) return;

    this.http.delete(`${this.apiUrl}/${file.id}`).subscribe({
        next: () => {
            if (this.selectedRecord()?.id === file.id) this.selectedRecord.set(null);
            this.checkedIds.update((ids) => ids.filter((x) => x !== file.id));
            this.showMessage('File deleted.', 'success');
            this.loadFiles();
        },
        error: () => this.showMessage('Delete failed.', 'error')
    });
}

deleteChecked(): void {
    const ids = this.checkedIds();
    if (ids.length === 0) return;
    if (!confirm('Delete ' + ids.length + ' file(s)? This cannot be undone.')) return;

    this.http.post<{ deleted: number }>(this.apiUrl + '/delete', ids).subscribe({
        next: (res) => {
            this.checkedIds.set([]);
            this.selectedRecord.set(null);
            this.showMessage(res.deleted + ' file(s) deleted.', 'success');
            this.loadFiles();
        },
        error: () => this.showMessage('Delete failed.', 'error')
    });
}






}